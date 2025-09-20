package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.DeliveryMethod
import com.oja.app.ui.LocalStrings

@Composable
fun TransporterSignupScreen(nav: NavHostController) {
    val strings = LocalStrings.current
    var name by remember { mutableStateOf("") }
    var bike by remember { mutableStateOf(true) }
    var bicycle by remember { mutableStateOf(false) }
    var car by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.transporter.title)
        Row { Checkbox(bicycle, { bicycle = it }); Text(strings.cart.methodLabel(DeliveryMethod.BICYCLE)) }
        Row { Checkbox(bike, { bike = it }); Text(strings.cart.methodLabel(DeliveryMethod.BIKE)) }
        Row { Checkbox(car, { car = it }); Text(strings.cart.methodLabel(DeliveryMethod.CAR)) }
        Spacer(Modifier.height(8.dp))
        Button({ nav.popBackStack() }) { Text(strings.transporter.submit) }
    }
}
