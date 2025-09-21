package com.oja.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PaymentsScreen(nav: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Payments (Test Mode)")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* trigger Paystack PaymentSheet with server access_code */ }) { Text("Pay with Paystack") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* open Flutterwave Drop-In */ }) { Text("Pay with Flutterwave") }
    }
}
