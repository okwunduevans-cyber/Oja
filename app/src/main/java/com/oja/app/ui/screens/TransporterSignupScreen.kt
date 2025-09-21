package com.oja.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.DeliveryMethod

@Composable
fun TransporterSignupScreen(nav: NavHostController) {
    var name by remember { mutableStateOf("") }
    var bike by remember { mutableStateOf(true) }
    var bicycle by remember { mutableStateOf(false) }
    var car by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Transporter Registration")
        Row { Checkbox(bicycle, { bicycle = it }); Text("Bicycle") }
        Row { Checkbox(bike, { bike = it }); Text("Bike") }
        Row { Checkbox(car, { car = it }); Text("Car") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.popBackStack() }) { Text("Submit") }
    }
}
