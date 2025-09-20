package com.oja.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

object Repo {
    private val _cart = MutableStateFlow(CartState())
    val cart: StateFlow<CartState> = _cart

    private val _jobs = MutableStateFlow<List<JobTicket>>(emptyList())
    val jobs: StateFlow<List<JobTicket>> = _jobs

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
        val jt = JobTicket(
            id = "j-${Random.nextInt(10000,99999)}",
            orderId = order.id,
            method = method,
            pickupLat = 6.449, pickupLng = 3.602,
            dropLat = 6.453, dropLng = 3.611
        )
        _jobs.update { listOf(jt) + it }
        _cart.value = CartState()
        return order
    }

    fun claimJob(jobId: String, transporterId: String) {
        _jobs.update { list -> list.map { if (it.id == jobId) it.copy(claimedByTransporterId = transporterId) else it } }
    }
}
