package com.oja.app.net

import com.oja.app.data.DeliveryMethod
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object OrdersApi {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    suspend fun createOrder(
        items: List<OrderItemDto>,
        method: DeliveryMethod,
        acceptedStoreIds: List<String>,
        clientOrderId: String
    ): OrderResponse? {
        val payload = OrderRequest(
            clientOrderId = clientOrderId,
            deliveryMethod = method.name.lowercase(),
            acceptedExtraStores = acceptedStoreIds,
            items = items
        )
        return runCatching {
            val response = client.post("${WsConfig.API_BASE_URL}/orders") {
                setBody(payload)
            }
            if (response.status.isSuccess()) {
                response.body<OrderResponse>()
            } else null
        }.getOrNull()
    }

    @Serializable
    data class OrderItemDto(
        @SerialName("product_id") val productId: String,
        val quantity: Int
    )

    @Serializable
    private data class OrderRequest(
        @SerialName("client_order_id") val clientOrderId: String,
        @SerialName("delivery_method") val deliveryMethod: String,
        @SerialName("accepted_extra_stores") val acceptedExtraStores: List<String>,
        val items: List<OrderItemDto>
    )

    @Serializable
    data class OrderResponse(
        @SerialName("order_id") val orderId: String,
        val status: String? = null
    )
}
