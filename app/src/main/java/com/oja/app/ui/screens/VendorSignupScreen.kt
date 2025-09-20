package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun VendorSignupScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vendor Onboarding")
        Spacer(Modifier.height(8.dp))
        Button({ nav.popBackStack() }) { Text("Submit") }
    }
}
