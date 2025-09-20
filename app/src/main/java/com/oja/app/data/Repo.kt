package com.oja.app.data

import com.oja.app.net.JobFeedState
import com.oja.app.net.JobSocketClient
import com.oja.app.net.OrdersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

object Repo {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _cart = MutableStateFlow(CartState())
    val cart: StateFlow<CartState> = _cart

    val jobs: StateFlow<List<JobTicket>> = JobSocketClient.jobs
    val jobFeedState: StateFlow<JobFeedState> = JobSocketClient.connectionState

    private val stores = listOf(
        Store("s1","Idumota Plastics"),
        Store("s2","Lekki Grocer"),
        Store("s3","Ajah Tools")
    )
    val products = listOf(
        Product("p1","s1","Broom", 1200),
        Product("p2","s2","Indomie Pack", 5000),
        Product("p3","s3","Spanner", 2500)
    )

    fun addToCart(p: Product) { _cart.update { it.copy(items = it.items + CartItem(p, 1)) } }
    fun acceptExtraFeeFor(storeId: String) { _cart.update { it.copy(acceptedExtraFees = it.acceptedExtraFees + storeId) } }
    fun clearCart() { _cart.value = CartState() }

    fun createOrder(method: DeliveryMethod): Order {
        val cs = _cart.value
        val storeIds = cs.groupedByStore.keys.toList()
        val base = cs.subtotal
        val extraFee = (storeIds.size - 1).coerceAtLeast(0) * 700
        val total = base + extraFee
        val order = Order(id = "o-${Random.nextInt(10000, 99999)}", storeIds, method, total)
        JobSocketClient.enqueueLocalShadowJob(order, method)
        scope.launch {
            OrdersApi.createOrder(
                items = cs.items.map { OrdersApi.OrderItemDto(productId = it.product.id, quantity = it.qty) },
                method = method,
                acceptedStoreIds = cs.acceptedExtraFees.toList(),
                clientOrderId = order.id
            )
        }
        _cart.value = CartState()
        return order
    }

    fun claimJob(jobId: String, transporterId: String) {
        JobSocketClient.claimJob(jobId, transporterId)
    }
}
