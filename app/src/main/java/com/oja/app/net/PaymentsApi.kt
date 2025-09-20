package com.oja.app.net

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object PaymentsApi {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }

    suspend fun initPaystack(request: PaystackInitRequest = PaystackInitRequest()): PaystackInitResponse {
        return client.post("${WsConfig.API_BASE_URL}/payments/paystack/init") {
            setBody(request)
        }.body()
    }

    suspend fun verifyPaystack(reference: String): PaymentVerifyResponse {
        return client.get("${WsConfig.API_BASE_URL}/payments/paystack/verify") {
            parameter("reference", reference)
        }.body()
    }

    suspend fun initFlutterwave(request: FlutterwaveInitRequest = FlutterwaveInitRequest()): FlutterwaveInitResponse {
        return client.post("${WsConfig.API_BASE_URL}/payments/flutterwave/init") {
            setBody(request)
        }.body()
    }

    suspend fun verifyFlutterwave(txRef: String): PaymentVerifyResponse {
        return client.get("${WsConfig.API_BASE_URL}/payments/flutterwave/verify") {
            parameter("tx_ref", txRef)
        }.body()
    }

    suspend fun initNqr(request: NqrInitRequest = NqrInitRequest()): NqrInitResponse {
        return client.post("${WsConfig.API_BASE_URL}/payments/nqr/init") {
            setBody(request)
        }.body()
    }

    suspend fun verifyNqr(reference: String): PaymentVerifyResponse {
        return client.get("${WsConfig.API_BASE_URL}/payments/nqr/verify") {
            parameter("reference", reference)
        }.body()
    }

    suspend fun initUssd(request: UssdInitRequest = UssdInitRequest()): UssdInitResponse {
        return client.post("${WsConfig.API_BASE_URL}/payments/ussd/init") {
            setBody(request)
        }.body()
    }

    suspend fun verifyUssd(reference: String): PaymentVerifyResponse {
        return client.get("${WsConfig.API_BASE_URL}/payments/ussd/verify") {
            parameter("reference", reference)
        }.body()
    }
}

@Serializable
data class PaystackInitRequest(
    @SerialName("email") val email: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("currency") val currency: String? = null
)

@Serializable
data class PaystackInitResponse(
    @SerialName("access_code") val accessCode: String,
    @SerialName("reference") val reference: String,
    @SerialName("authorization_url") val authorizationUrl: String? = null
)

@Serializable
data class FlutterwaveInitRequest(
    @SerialName("amount") val amount: Double? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("email") val email: String? = null
)

@Serializable
data class FlutterwaveInitResponse(
    @SerialName("public_key") val publicKey: String,
    @SerialName("encryption_key") val encryptionKey: String,
    @SerialName("tx_ref") val txRef: String,
    @SerialName("amount") val amount: Double,
    @SerialName("currency") val currency: String,
    @SerialName("customer_email") val customerEmail: String,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("redirect_url") val redirectUrl: String? = WsConfig.FLUTTERWAVE_REDIRECT_URL
)

@Serializable
data class PaymentVerifyResponse(
    @SerialName("status") val status: String,
    @SerialName("reference") val reference: String,
    @SerialName("message") val message: String? = null
)

@Serializable
data class NqrInitRequest(
    @SerialName("amount") val amount: Long? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_phone") val customerPhone: String? = null
)

@Serializable
data class NqrInitResponse(
    @SerialName("reference") val reference: String,
    @SerialName("qr_data") val qrData: String,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("customer_name") val customerName: String? = null
)

@Serializable
data class UssdInitRequest(
    @SerialName("amount") val amount: Long? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("customer_phone") val customerPhone: String? = null,
    @SerialName("bank_code") val bankCode: String? = null
)

@Serializable
data class UssdInitResponse(
    @SerialName("reference") val reference: String,
    @SerialName("ussd_code") val ussdCode: String,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("currency") val currency: String? = null
)
