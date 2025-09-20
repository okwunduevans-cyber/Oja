package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.*
import com.oja.app.navigation.Route

@Composable
fun CartScreen(nav: NavHostController) {
    val cart by Repo.cart.collectAsState()
    var method by remember { mutableStateOf(DeliveryMethod.BIKE) }
    var pendingStoreId by remember { mutableStateOf<String?>(null) }

    val groups = cart.groupedByStore
    val extraStores = (groups.keys.size - 1).coerceAtLeast(0)
    val extraFee = extraStores * 700L
    val needsAccept = groups.keys.any { it !in cart.acceptedExtraFees } && groups.keys.size > 1

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            groups.forEach { (storeId, items) ->
                item { Text("Store: $storeId") }
                items(items.size) { i ->
                    val it = items[i]
                    Text("${it.product.name} x${it.qty} — ₦${it.product.price}")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button({ method = DeliveryMethod.BICYCLE }) { Text("Bicycle") }
            Button({ method = DeliveryMethod.BIKE }) { Text("Bike") }
            Button({ method = DeliveryMethod.CAR }) { Text("Car") }
        }
        Spacer(Modifier.height(8.dp))
        Text("Subtotal: ₦${cart.subtotal}")
        if (extraStores > 0) Text("Extra store fee: ₦$extraFee")
        Spacer(Modifier.height(8.dp))
        Button({
            if (needsAccept) {
                pendingStoreId = groups.keys.first { it !in cart.acceptedExtraFees }
            } else {
                val order = Repo.createOrder(method)
                nav.navigate(Route.Track.path(order.id))
            }
        }, enabled = cart.items.isNotEmpty()) { Text("Checkout") }
    }

    val sid = pendingStoreId
    if (sid != null) {
        AlertDialog(
            onDismissRequest = { pendingStoreId = null },
            title = { Text("Extra logistics cost") },
            text = { Text("Adding items from $sid adds courier cost. Accept?") },
            confirmButton = {
                TextButton({
                    Repo.acceptExtraFeeFor(sid)
                    pendingStoreId = null
                }) { Text("Accept") }
            },
            dismissButton = { TextButton({ pendingStoreId = null }) { Text("Cancel") } }
        )
    }
}
