package com.oja.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.Repo
import com.oja.app.navigation.Route

@Composable
fun HomeScreen(nav: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { nav.navigate(Route.Cart.path) }) { Text("Cart") }
            Button(onClick = { nav.navigate(Route.Jobs.path) }) { Text("Jobs") }
            Button(onClick = { nav.navigate(Route.TransporterSignup.path) }) { Text("Be a Transporter") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Repo.products.size) { idx ->
                val product = Repo.products[idx]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(product.name)
                        Text("₦${'$'}{product.price}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { Repo.addToCart(product) }) { Text("Add to Cart") }
                    }
                }
            }
        }
    }
}
