package com.oja.app.net

import android.util.Log
import com.oja.app.data.DeliveryMethod
import com.oja.app.data.JobTicket
import com.oja.app.data.Order
import com.oja.app.ui.OjaApp
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException
import java.io.File
import java.io.IOException
import kotlin.math.min

object JobSocketClient {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
    private val json = Json { ignoreUnknownKeys = true }

    private val cacheFile: File by lazy {
        File(OjaApp.appContext.filesDir, "jobs-cache.json")
    }

    private val _jobs = MutableStateFlow<List<JobTicket>>(emptyList())
    val jobs: StateFlow<List<JobTicket>> = _jobs.asStateFlow()

    private val _connectionState = MutableStateFlow<JobFeedState>(JobFeedState.Idle)
    val connectionState: StateFlow<JobFeedState> = _connectionState.asStateFlow()

    private var session: DefaultClientWebSocketSession? = null

    init {
        loadCache()
        scope.launch { runSocketLoop() }
    }

    fun enqueueLocalShadowJob(order: Order, method: DeliveryMethod) {
        val shadow = JobTicket(
            id = "shadow-${order.id}",
            orderId = order.id,
            method = method,
            pickupLat = 6.449,
            pickupLng = 3.602,
            dropLat = 6.453,
            dropLng = 3.611,
            status = "pending"
        )
        _jobs.update { listOf(shadow) + it.filterNot { job -> job.id == shadow.id } }
        persistCache()
    }

    fun claimJob(jobId: String, transporterId: String) {
        scope.launch {
            val payload = ActionEnvelope(
                action = "claim",
                data = mapOf(
                    "jobId" to jobId,
                    "transporterId" to transporterId
                )
            )
            sendEnvelope(payload)
        }
    }

    private suspend fun sendEnvelope(envelope: ActionEnvelope) {
        val text = json.encodeToString(ActionEnvelope.serializer(), envelope)
        try {
            session?.send(Frame.Text(text))
        } catch (t: Throwable) {
            Log.w("JobSocketClient", "Unable to send message", t)
        }
    }

    private suspend fun runSocketLoop() {
        var attempt = 0
        while (scope.isActive) {
            try {
                attempt++
                _connectionState.value = JobFeedState.Connecting(attempt)
                client.webSocket(urlString = WsConfig.WS_URL) {
                    session = this
                    attempt = 0
                    _connectionState.value = JobFeedState.Connected
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            handlePayload(frame.readText())
                        }
                    }
                }
            } catch (t: Throwable) {
                Log.w("JobSocketClient", "WebSocket error", t)
                _connectionState.value = JobFeedState.Disconnected(t)
                val delayMs = min(15_000L, 1_000L * attempt)
                delay(delayMs)
            } finally {
                session = null
            }
        }
    }

    private fun handlePayload(raw: String) {
        try {
            val envelope = json.decodeFromString(JobEnvelope.serializer(), raw)
            when (envelope.type) {
                "snapshot" -> {
                    val jobs = envelope.jobs.orEmpty().map { it.toTicket() }
                    _jobs.value = jobs
                    persistCache()
                }
                "job_update" -> {
                    envelope.job?.let { updateJob(it.toTicket()) }
                }
                "job_removed" -> {
                    envelope.job?.let { removeJob(it.id) }
                }
                "ping" -> Unit
                else -> Log.d("JobSocketClient", "Unhandled event ${envelope.type}")
            }
        } catch (err: JsonDecodingException) {
            Log.e("JobSocketClient", "Unable to parse job payload", err)
        }
    }

    private fun updateJob(ticket: JobTicket) {
        _jobs.update { current ->
            val filtered = current.filterNot { it.orderId == ticket.orderId || it.id == ticket.id || it.id.startsWith("shadow-") && it.orderId == ticket.orderId }
            listOf(ticket) + filtered
        }
        persistCache()
    }

    private fun removeJob(jobId: String) {
        _jobs.update { list -> list.filterNot { it.id == jobId } }
        persistCache()
    }

    private fun loadCache() {
        if (!cacheFile.exists()) return
        runCatching {
            val cached = json.decodeFromString(JobCache.serializer(), cacheFile.readText())
            _jobs.value = cached.jobs.map { it.toTicket() }
        }.onFailure { cacheFile.delete() }
    }

    private fun persistCache() {
        runCatching {
            val dto = JobCache(_jobs.value.map { it.toPayload() })
            cacheFile.writeText(json.encodeToString(JobCache.serializer(), dto))
        }
    }
}

sealed interface JobFeedState {
    data object Idle : JobFeedState
    data class Connecting(val attempt: Int) : JobFeedState
    data object Connected : JobFeedState
    data class Disconnected(val cause: Throwable) : JobFeedState
}

@Serializable
private data class JobEnvelope(
    val type: String,
    val jobs: List<JobPayload>? = null,
    val job: JobPayload? = null
)

@Serializable
private data class JobPayload(
    val id: String,
    @SerialName("order_id") val orderId: String,
    val method: String,
    @SerialName("pickup_lat") val pickupLat: Double,
    @SerialName("pickup_lng") val pickupLng: Double,
    @SerialName("drop_lat") val dropLat: Double,
    @SerialName("drop_lng") val dropLng: Double,
    @SerialName("claimed_by") val claimedBy: String? = null,
    val status: String? = null,
    val courier: CourierPayload? = null
) {
    fun toTicket(): JobTicket {
        val delivery = runCatching { DeliveryMethod.valueOf(method.uppercase()) }.getOrDefault(DeliveryMethod.BIKE)
        return JobTicket(
            id = id,
            orderId = orderId,
            method = delivery,
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            dropLat = dropLat,
            dropLng = dropLng,
            claimedByTransporterId = claimedBy,
            courierLat = courier?.lat,
            courierLng = courier?.lng,
            progress = courier?.progress,
            status = status
        )
    }
}

@Serializable
private data class CourierPayload(
    val lat: Double,
    val lng: Double,
    val progress: Double? = null
)

@Serializable
private data class JobCache(val jobs: List<JobPayload>)

private fun JobTicket.toPayload(): JobPayload = JobPayload(
    id = id,
    orderId = orderId,
    method = method.name.lowercase(),
    pickupLat = pickupLat,
    pickupLng = pickupLng,
    dropLat = dropLat,
    dropLng = dropLng,
    claimedBy = claimedByTransporterId,
    status = status,
    courier = if (courierLat != null && courierLng != null) CourierPayload(courierLat, courierLng, progress) else null
)

@Serializable
private data class ActionEnvelope(
    val action: String,
    val data: Map<String, String>
)
