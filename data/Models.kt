package data

import java.time.Instant

/**
 * Supported delivery options across the marketplace along with the metadata
 * the UI needs for ETA messaging and baseline fee calculations.
 */
enum class DeliveryMethod(
    val label: String,
    val etaMinutes: IntRange,
    val baseFee: Int
) {
    BICYCLE(
        label = "Bicycle Courier",
        etaMinutes = 35..55,
        baseFee = 600
    ),
    MOTORBIKE(
        label = "Bike Courier",
        etaMinutes = 25..45,
        baseFee = 900
    ),
    CAR(
        label = "Car Courier",
        etaMinutes = 40..65,
        baseFee = 1300
    );

    val etaLabel: String get() = "${etaMinutes.first}-${etaMinutes.last} mins"
}

/**
 * Track the lifecycle of an order as it advances from the cart to delivery.
 */
enum class OrderStatus {
    DRAFT,
    SUBMITTED,
    PREPARING,
    READY_FOR_PICKUP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}

/**
 * Job tickets surface to transporters and operations. They progress from
 * unclaimed to delivered as riders take ownership and complete the run.
 */
enum class JobState {
    UNCLAIMED,
    CLAIMED,
    EN_ROUTE,
    DELIVERED
}

/**
 * Basic representation of a geographic address used for both pickups and drop-offs.
 */
data class Address(
    val label: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val additionalDirections: String? = null
)

/**
 * Stores are the origin point for catalogue items and pickups.
 */
data class Store(
    val id: String,
    val name: String,
    val category: String,
    val pickupAddress: Address
)

/**
 * A product sold by a store. Price is in Kobo to avoid floating point rounding issues.
 */
data class Product(
    val id: String,
    val storeId: String,
    val name: String,
    val unitPriceKobo: Long,
    val imageUrl: String? = null,
    val unitLabel: String = "unit"
)

/**
 * Single line item in a cart representing a quantity of a product.
 */
data class CartLine(
    val product: Product,
    val quantity: Int
) {
    init {
        require(quantity > 0) { "Quantity must be greater than zero" }
    }

    val lineTotalKobo: Long get() = product.unitPriceKobo * quantity
}

/**
 * Buyer cart with helper logic for surcharge acceptance and totals.
 */
data class Cart(
    val items: List<CartLine> = emptyList(),
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MOTORBIKE,
    val dropOffAddress: Address? = null,
    val acceptedExtraStoreIds: Set<String> = emptySet()
) {
    val subtotalKobo: Long get() = items.sumOf(CartLine::lineTotalKobo)

    val storeSequence: List<String> get() = items.map { it.product.storeId }.distinct()

    val primaryStoreId: String? get() = storeSequence.firstOrNull()

    val extraStoreIds: Set<String> get() = storeSequence.drop(1).toSet()

    val pendingAcceptanceStoreIds: Set<String>
        get() = extraStoreIds - acceptedExtraStoreIds

    val hasPendingSurchargeAcceptance: Boolean get() = pendingAcceptanceStoreIds.isNotEmpty()

    fun markSurchargeAccepted(storeId: String): Cart =
        copy(acceptedExtraStoreIds = acceptedExtraStoreIds + storeId)

    fun toggleSurchargeAcceptance(storeId: String): Cart =
        if (storeId in acceptedExtraStoreIds) {
            copy(acceptedExtraStoreIds = acceptedExtraStoreIds - storeId)
        } else {
            markSurchargeAccepted(storeId)
        }

    fun removeStoreAcceptance(storeId: String): Cart =
        copy(acceptedExtraStoreIds = acceptedExtraStoreIds - storeId)

    fun surchargeKobo(extraStoreFeeKobo: Long): Long = extraStoreIds.size * extraStoreFeeKobo

    fun deliveryFeeKobo(): Long = deliveryMethod.baseFee * 100L

    fun totalKobo(extraStoreFeeKobo: Long): Long =
        subtotalKobo + deliveryFeeKobo() + surchargeKobo(extraStoreFeeKobo)

    fun upsertLine(product: Product, quantity: Int): Cart {
        require(quantity > 0) { "Quantity must be greater than zero" }
        val updatedItems = items.toMutableList()
        val index = updatedItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            updatedItems[index] = CartLine(product, quantity)
        } else {
            updatedItems += CartLine(product, quantity)
        }
        val newCart = copy(items = updatedItems)
        val stillInCartStoreIds = newCart.items.map { it.product.storeId }.toSet()
        val filteredAcceptance = newCart.acceptedExtraStoreIds.filterTo(mutableSetOf()) { it in stillInCartStoreIds }
        return newCart.copy(acceptedExtraStoreIds = filteredAcceptance)
    }

    fun removeLine(productId: String): Cart {
        val filteredItems = items.filterNot { it.product.id == productId }
        val newCart = copy(items = filteredItems)
        val remainingStoreIds = newCart.items.map { it.product.storeId }.toSet()
        val filteredAcceptance = newCart.acceptedExtraStoreIds.filterTo(mutableSetOf()) { it in remainingStoreIds }
        return newCart.copy(acceptedExtraStoreIds = filteredAcceptance)
    }
}

/**
 * Order snapshot captured at checkout.
 */
data class Order(
    val id: String,
    val status: OrderStatus,
    val items: List<CartLine>,
    val storeSequence: List<String>,
    val deliveryMethod: DeliveryMethod,
    val dropOffAddress: Address,
    val subtotalKobo: Long,
    val deliveryFeeKobo: Long,
    val surchargeKobo: Long,
    val totalKobo: Long,
    val createdAt: Instant,
    val extraStoreIds: Set<String>
) {
    val storeCount: Int get() = storeSequence.size

    val requiresMultiStoreSurcharge: Boolean get() = extraStoreIds.isNotEmpty()
}

/**
 * Each pickup stop on a transporter run.
 */
data class PickupStop(
    val store: Store,
    val readyInMinutes: Int
)

/**
 * Ticket surfaced on the Jobs board.
 */
data class JobTicket(
    val id: String,
    val orderId: String,
    val pickups: List<PickupStop>,
    val dropOff: Address,
    val method: DeliveryMethod,
    val payoutKobo: Long,
    val state: JobState,
    val assignedTransporterId: String? = null,
    val lastLocationPingAt: Instant? = null
) {
    val isClaimed: Boolean get() = assignedTransporterId != null

    val isActive: Boolean get() = state == JobState.CLAIMED || state == JobState.EN_ROUTE
}
