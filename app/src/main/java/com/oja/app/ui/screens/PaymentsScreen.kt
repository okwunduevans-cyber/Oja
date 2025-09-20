package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PaymentsScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Payments (Test Mode)")
        Spacer(Modifier.height(8.dp))
        Button({ /* trigger Paystack PaymentSheet with server access_code */ }) { Text("Pay with Paystack") }
        Spacer(Modifier.height(8.dp))
        Button({ /* open Flutterwave Drop-In */ }) { Text("Pay with Flutterwave") }
    }
}
