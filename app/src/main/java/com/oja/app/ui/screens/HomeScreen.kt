package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.Repo
import com.oja.app.navigation.Route
import com.oja.app.ui.LocalStrings

@Composable
fun HomeScreen(nav: NavHostController) {
    val strings = LocalStrings.current
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button({ nav.navigate(Route.Cart.path) }) { Text(strings.navigation.cart) }
            Button({ nav.navigate(Route.Jobs.path) }) { Text(strings.navigation.jobs) }
            Button({ nav.navigate(Route.TransporterSignup.path) }) { Text(strings.navigation.transporter) }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Repo.products.size) { idx ->
                val p = Repo.products[idx]
                Card(Modifier.fillMaxWidth().padding(2.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name)
                        Text("₦${'$'}{p.price}")
                        Spacer(Modifier.height(8.dp))
                        Button({ Repo.addToCart(p) }) { Text(strings.home.addToCart) }
                    }
                }
            }
        }
    }
}
