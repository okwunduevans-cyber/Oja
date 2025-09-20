package com.oja.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.oja.app.net.FlutterwaveInitResponse
import com.oja.app.net.NqrInitResponse
import com.oja.app.net.PaymentsApi
import com.oja.app.net.PaystackInitResponse
import com.oja.app.net.UssdInitResponse
import com.oja.app.ui.LocalStrings
import kotlinx.coroutines.launch

@Composable
fun PaymentsScreen(nav: NavHostController) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = rememberSnackbarHostState()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val currentStrings by rememberUpdatedState(strings)

    var isLoading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<PaymentStatus?>(null) }
    var paystackInit by remember { mutableStateOf<PaystackInitResponse?>(null) }
    var flutterwaveInit by remember { mutableStateOf<FlutterwaveInitResponse?>(null) }
    var nqrPayload by remember { mutableStateOf<NqrInitResponse?>(null) }
    var ussdPayload by remember { mutableStateOf<UssdInitResponse?>(null) }
    var consentChecked by remember { mutableStateOf(false) }

    val qrSizePx = remember(density) { with(density) { 220.dp.roundToPx() } }
    val qrImage = remember(nqrPayload?.qrData, qrSizePx) {
        nqrPayload?.qrData?.let { data -> generateQrBitmap(data, qrSizePx) }
    }

    val paystackLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val reference = result.data?.getStringExtra(EXTRA_PAYSTACK_REFERENCE) ?: paystackInit?.reference
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                if (reference != null) {
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.verifyPaystack(reference) }
                        outcome.onSuccess {
                            isLoading = false
                            status = PaymentStatus.Success(
                                gateway = "Paystack",
                                reference = it.reference,
                                detail = it.message ?: it.status
                            )
                        }
                        outcome.onFailure {
                            isLoading = false
                            status = PaymentStatus.Error(
                                gateway = "Paystack",
                                message = currentStrings.payments.verificationFailed("Paystack"),
                                detail = it.message
                            )
                        }
                    }
                } else {
                    isLoading = false
                    status = PaymentStatus.Error(
                        gateway = "Paystack",
                        message = strings.payments.missingReference("Paystack")
                    )
                }
            }
            Activity.RESULT_CANCELED -> {
                isLoading = false
                status = PaymentStatus.Canceled("Paystack")
            }
            else -> {
                isLoading = false
                status = PaymentStatus.Error(
                    gateway = "Paystack",
                    message = strings.payments.unexpectedResult("Paystack", result.resultCode)
                )
            }
        }
    }

    val flutterwaveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val txRef = result.data?.getStringExtra(EXTRA_FLW_TX_REF) ?: flutterwaveInit?.txRef
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                if (txRef != null) {
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.verifyFlutterwave(txRef) }
                        outcome.onSuccess {
                            isLoading = false
                            status = PaymentStatus.Success(
                                gateway = "Flutterwave",
                                reference = it.reference,
                                detail = it.message ?: it.status
                            )
                        }
                        outcome.onFailure {
                            isLoading = false
                            status = PaymentStatus.Error(
                                gateway = "Flutterwave",
                                message = currentStrings.payments.verificationFailed("Flutterwave"),
                                detail = it.message
                            )
                        }
                    }
                } else {
                    isLoading = false
                    status = PaymentStatus.Error(
                        gateway = "Flutterwave",
                        message = strings.payments.missingReference("Flutterwave")
                    )
                }
            }
            Activity.RESULT_CANCELED -> {
                isLoading = false
                status = PaymentStatus.Canceled("Flutterwave")
            }
            else -> {
                isLoading = false
                status = PaymentStatus.Error(
                    gateway = "Flutterwave",
                    message = strings.payments.unexpectedResult("Flutterwave", result.resultCode)
                )
            }
        }
    }

    LaunchedEffect(status, strings) {
        status?.let { snackbarHostState.showSnackbar(statusMessage(it, strings)) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(strings.payments.title, style = MaterialTheme.typography.headlineSmall)
            Text(strings.payments.stagingHint)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = consentChecked, onCheckedChange = { consentChecked = it })
                Spacer(Modifier.width(8.dp))
                Text(strings.payments.consentLabel, style = MaterialTheme.typography.bodyMedium)
            }
            Text(strings.payments.consentDetails, style = MaterialTheme.typography.bodySmall)

            if (isLoading) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    if (activity == null) {
                        status = PaymentStatus.Error(
                            gateway = "Paystack",
                            message = strings.payments.requireActivity("Paystack")
                        )
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.initPaystack() }
                        outcome.onSuccess { response ->
                            paystackInit = response
                            val intent = buildPaystackIntent(activity, response)
                            if (intent != null) {
                                paystackLauncher.launch(intent)
                            } else {
                                isLoading = false
                                status = PaymentStatus.Error(
                                    gateway = "Paystack",
                                    message = currentStrings.payments.sdkMissing("Paystack")
                                )
                            }
                        }
                        outcome.onFailure {
                            isLoading = false
                            status = PaymentStatus.Error(
                                gateway = "Paystack",
                                message = currentStrings.payments.initError("Paystack"),
                                detail = it.message
                            )
                        }
                    }
                },
                enabled = !isLoading && consentChecked
            ) { Text(strings.payments.paystackButton) }

            Button(
                onClick = {
                    if (activity == null) {
                        status = PaymentStatus.Error(
                            gateway = "Flutterwave",
                            message = strings.payments.requireActivity("Flutterwave")
                        )
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.initFlutterwave() }
                        outcome.onSuccess { response ->
                            flutterwaveInit = response
                            val intent = buildFlutterwaveIntent(activity, response)
                            if (intent != null) {
                                flutterwaveLauncher.launch(intent)
                            } else {
                                isLoading = false
                                status = PaymentStatus.Error(
                                    gateway = "Flutterwave",
                                    message = currentStrings.payments.sdkMissing("Flutterwave")
                                )
                            }
                        }
                        outcome.onFailure {
                            isLoading = false
                            status = PaymentStatus.Error(
                                gateway = "Flutterwave",
                                message = currentStrings.payments.initError("Flutterwave"),
                                detail = it.message
                            )
                        }
                    }
                },
                enabled = !isLoading && consentChecked
            ) { Text(strings.payments.flutterwaveButton) }

            Divider()

            Text(strings.payments.nqrTitle, style = MaterialTheme.typography.titleMedium)
            Text(strings.payments.qrHint, style = MaterialTheme.typography.bodySmall)
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.initNqr() }
                        outcome.onSuccess {
                            nqrPayload = it
                            status = PaymentStatus.Info(
                                gateway = "NQR",
                                message = "${currentStrings.payments.referenceLabel}: ${it.reference}",
                                detail = currentStrings.payments.qrHint
                            )
                        }
                        outcome.onFailure {
                            status = PaymentStatus.Error(
                                gateway = "NQR",
                                message = currentStrings.payments.initError("NQR"),
                                detail = it.message
                            )
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading && consentChecked
            ) { Text(strings.payments.nqrButton) }

            nqrPayload?.let { payload ->
                qrImage?.let { image ->
                    Image(
                        bitmap = image,
                        contentDescription = strings.payments.nqrTitle,
                        modifier = Modifier.height(220.dp)
                    )
                }
                SelectionContainer {
                    Text(
                        text = "${strings.payments.referenceLabel}: ${payload.reference}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                payload.expiresAt?.let { expiry ->
                    Text(
                        text = "${strings.payments.expiresLabel}: $expiry",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val outcome = runCatching { PaymentsApi.verifyNqr(payload.reference) }
                            outcome.onSuccess {
                                isLoading = false
                                status = PaymentStatus.Success(
                                    gateway = "NQR",
                                    reference = it.reference,
                                    detail = it.message ?: it.status
                                )
                            }
                            outcome.onFailure {
                                isLoading = false
                                status = PaymentStatus.Error(
                                    gateway = "NQR",
                                    message = currentStrings.payments.verificationFailed("NQR"),
                                    detail = it.message
                                )
                            }
                        }
                    },
                    enabled = !isLoading && consentChecked
                ) { Text(strings.payments.nqrVerifyButton) }
            }

            Divider()

            Text(strings.payments.ussdTitle, style = MaterialTheme.typography.titleMedium)
            Text(strings.payments.ussdHint, style = MaterialTheme.typography.bodySmall)
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val outcome = runCatching { PaymentsApi.initUssd() }
                        outcome.onSuccess {
                            ussdPayload = it
                            status = PaymentStatus.Info(
                                gateway = "USSD",
                                message = "${currentStrings.payments.ussdCodeLabel}: ${it.ussdCode}",
                                detail = currentStrings.payments.ussdHint
                            )
                        }
                        outcome.onFailure {
                            status = PaymentStatus.Error(
                                gateway = "USSD",
                                message = currentStrings.payments.initError("USSD"),
                                detail = it.message
                            )
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading && consentChecked
            ) { Text(strings.payments.ussdButton) }

            ussdPayload?.let { payload ->
                SelectionContainer {
                    Text(
                        text = "${strings.payments.ussdCodeLabel}: ${payload.ussdCode}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                SelectionContainer {
                    Text(
                        text = "${strings.payments.referenceLabel}: ${payload.reference}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                payload.bankName?.let { bank ->
                    Text(
                        text = "${strings.payments.bankLabel}: $bank",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                payload.expiresAt?.let { expiry ->
                    Text(
                        text = "${strings.payments.expiresLabel}: $expiry",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val outcome = runCatching { PaymentsApi.verifyUssd(payload.reference) }
                            outcome.onSuccess {
                                isLoading = false
                                status = PaymentStatus.Success(
                                    gateway = "USSD",
                                    reference = it.reference,
                                    detail = it.message ?: it.status
                                )
                            }
                            outcome.onFailure {
                                isLoading = false
                                status = PaymentStatus.Error(
                                    gateway = "USSD",
                                    message = currentStrings.payments.verificationFailed("USSD"),
                                    detail = it.message
                                )
                            }
                        }
                    },
                    enabled = !isLoading && consentChecked
                ) { Text(strings.payments.ussdVerifyButton) }
            }

            status?.let { state ->
                val color = when (state) {
                    is PaymentStatus.Success -> MaterialTheme.colorScheme.tertiary
                    is PaymentStatus.Canceled -> MaterialTheme.colorScheme.secondary
                    is PaymentStatus.Error -> MaterialTheme.colorScheme.error
                    is PaymentStatus.Info -> MaterialTheme.colorScheme.primary
                }
                Text(statusMessage(state, strings), color = color)
                state.detail?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun statusMessage(status: PaymentStatus, strings: com.oja.app.ui.AppStrings): String {
    return when (status) {
        is PaymentStatus.Success -> strings.payments.success(status.gateway, status.reference)
        is PaymentStatus.Canceled -> strings.payments.cancelled(status.gateway)
        is PaymentStatus.Error -> status.message
        is PaymentStatus.Info -> status.message
    }
}

private fun buildPaystackIntent(activity: Activity, payload: PaystackInitResponse): Intent? {
    return try {
        val clazz = Class.forName("com.paystack.android.paystack_ui.ui.PaystackPaymentActivity")
        Intent(activity, clazz).apply {
            putExtra(EXTRA_PAYSTACK_ACCESS_CODE, payload.accessCode)
            putExtra(EXTRA_PAYSTACK_REFERENCE, payload.reference)
        }
    } catch (error: Throwable) {
        null
    }
}

private fun buildFlutterwaveIntent(activity: Activity, payload: FlutterwaveInitResponse): Intent? {
    return try {
        val clazz = Class.forName("com.flutterwave.raveandroid.RavePayActivity")
        Intent(activity, clazz).apply {
            putExtra(EXTRA_FLW_PUBLIC_KEY, payload.publicKey)
            putExtra(EXTRA_FLW_ENCRYPTION_KEY, payload.encryptionKey)
            putExtra(EXTRA_FLW_TX_REF, payload.txRef)
            putExtra(EXTRA_FLW_AMOUNT, payload.amount)
            putExtra(EXTRA_FLW_CURRENCY, payload.currency)
            putExtra(EXTRA_FLW_EMAIL, payload.customerEmail)
            payload.customerName?.let { putExtra(EXTRA_FLW_CUSTOMER_NAME, it) }
            payload.redirectUrl?.let { putExtra(EXTRA_FLW_REDIRECT_URL, it) }
        }
    } catch (error: Throwable) {
        null
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun generateQrBitmap(data: String, size: Int): ImageBitmap? {
    return runCatching {
        val matrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }.asImageBitmap()
    }.getOrNull()
}

private sealed interface PaymentStatus {
    val gateway: String
    val detail: String?

    data class Success(override val gateway: String, val reference: String, override val detail: String?) : PaymentStatus
    data class Error(override val gateway: String, val message: String, override val detail: String? = null) : PaymentStatus
    data class Canceled(override val gateway: String) : PaymentStatus {
        override val detail: String? = null
    }
    data class Info(override val gateway: String, val message: String, override val detail: String? = null) : PaymentStatus
}

private const val EXTRA_PAYSTACK_ACCESS_CODE = "access_code"
private const val EXTRA_PAYSTACK_REFERENCE = "reference"
private const val EXTRA_FLW_PUBLIC_KEY = "PUBLIC_KEY"
private const val EXTRA_FLW_ENCRYPTION_KEY = "ENCRYPTION_KEY"
private const val EXTRA_FLW_TX_REF = "txRef"
private const val EXTRA_FLW_AMOUNT = "amount"
private const val EXTRA_FLW_CURRENCY = "currency"
private const val EXTRA_FLW_EMAIL = "email"
private const val EXTRA_FLW_CUSTOMER_NAME = "customer_name"
private const val EXTRA_FLW_REDIRECT_URL = "redirect_url"
