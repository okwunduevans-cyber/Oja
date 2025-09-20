package com.oja.app.data

enum class DeliveryMethod { BICYCLE, BIKE, CAR }

data class Store(val id: String, val name: String)
data class Product(val id: String, val storeId: String, val name: String, val price: Long)

data class CartItem(val product: Product, val qty: Int)
data class CartState(
    val items: List<CartItem> = emptyList(),
    val acceptedExtraFees: Set<String> = emptySet()
) {
    val groupedByStore: Map<String, List<CartItem>> get() = items.groupBy { it.product.storeId }
    val subtotal: Long get() = items.sumOf { it.product.price * it.qty }
}

data class Order(val id: String, val storeIds: List<String>, val method: DeliveryMethod, val total: Long)

data class JobTicket(
    val id: String,
    val orderId: String,
    val method: DeliveryMethod,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val claimedByTransporterId: String? = null
)

data class TransporterProfile(val id: String, val name: String, val methods: Set<DeliveryMethod>)
