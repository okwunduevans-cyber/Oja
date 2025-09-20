package data

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Central in-memory repository that feeds the UI. All state containers sit here so
 * screens can share a single source of truth during the MVP cycle.
 */
object Repo {
    private const val EXTRA_STORE_SURCHARGE_KOBO: Long = 50000L // ₦500

    private val yabaAddress = Address(
        label = "Tejuosho Ultra Modern Market, Yaba",
        latitude = 6.5079,
        longitude = 3.3711
    )
    private val ojotaAddress = Address(
        label = "Ojota New Garage Market",
        latitude = 6.5882,
        longitude = 3.3869
    )
    private val lekkiAddress = Address(
        label = "Lekki Phase 1 Farmers Market",
        latitude = 6.4402,
        longitude = 3.4832
    )

    private val storeCatalog = listOf(
        Store(
            id = "store_grocer_jola",
            name = "Jola's Groceries",
            category = "Groceries",
            pickupAddress = yabaAddress
        ),
        Store(
            id = "store_farmers_green",
            name = "Green Harvest Stall",
            category = "Fresh Produce",
            pickupAddress = ojotaAddress
        ),
        Store(
            id = "store_readymeals_spice",
            name = "Spice Route Kitchen",
            category = "Ready Meals",
            pickupAddress = lekkiAddress
        )
    )

    private val productCatalog = listOf(
        Product(
            id = "prod_ofada_rice",
            storeId = "store_grocer_jola",
            name = "Ofada Rice (2kg)",
            unitPriceKobo = 520_00L,
            unitLabel = "bag"
        ),
        Product(
            id = "prod_palm_oil",
            storeId = "store_grocer_jola",
            name = "Fresh Palm Oil (1L)",
            unitPriceKobo = 300_00L,
            unitLabel = "bottle"
        ),
        Product(
            id = "prod_ugwu_bundle",
            storeId = "store_farmers_green",
            name = "Ugu Leaf Bundle",
            unitPriceKobo = 120_00L,
            unitLabel = "bundle"
        ),
        Product(
            id = "prod_pepper_mix",
            storeId = "store_farmers_green",
            name = "Pepper Mix Basket",
            unitPriceKobo = 95_00L,
            unitLabel = "basket"
        ),
        Product(
            id = "prod_jollof_bowl",
            storeId = "store_readymeals_spice",
            name = "Smoky Party Jollof",
            unitPriceKobo = 250_00L,
            unitLabel = "bowl"
        ),
        Product(
            id = "prod_moi_moi_pack",
            storeId = "store_readymeals_spice",
            name = "Moi Moi Family Pack",
            unitPriceKobo = 185_00L,
            unitLabel = "pack"
        )
    )

    private val productIndex = productCatalog.associateBy(Product::id)
    private val storeIndex = storeCatalog.associateBy(Store::id)

    val stores: List<Store> = storeCatalog
    val products: List<Product> = productCatalog
    val productsByStore: Map<String, List<Product>> = productCatalog.groupBy(Product::storeId)

    val cartState = MutableStateFlow(Cart())
    val ordersState = MutableStateFlow<List<Order>>(emptyList())
    val jobTicketsState = MutableStateFlow<List<JobTicket>>(emptyList())

    fun clearCart() {
        cartState.value = Cart(deliveryMethod = cartState.value.deliveryMethod)
    }

    fun setDeliveryMethod(method: DeliveryMethod) {
        cartState.update { it.copy(deliveryMethod = method) }
    }

    fun setDropOffAddress(address: Address) {
        cartState.update { it.copy(dropOffAddress = address) }
    }

    fun toggleExtraStoreAcceptance(storeId: String) {
        cartState.update { it.toggleSurchargeAcceptance(storeId) }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        val product = productIndex[productId] ?: return
        cartState.update { current ->
            val existing = current.items.firstOrNull { it.product.id == productId }
            val newQuantity = (existing?.quantity ?: 0) + quantity
            current.upsertLine(product, newQuantity)
        }
    }

    fun setCartQuantity(productId: String, quantity: Int) {
        val product = productIndex[productId] ?: return
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        cartState.update { current -> current.upsertLine(product, quantity) }
    }

    fun removeFromCart(productId: String) {
        cartState.update { current -> current.removeLine(productId) }
    }

    fun pendingSurchargeStoreIds(): Set<String> = cartState.value.pendingAcceptanceStoreIds

    fun computedTotals(): CartTotals = with(cartState.value) {
        CartTotals(
            subtotalKobo = subtotalKobo,
            deliveryFeeKobo = deliveryFeeKobo(),
            surchargeKobo = surchargeKobo(EXTRA_STORE_SURCHARGE_KOBO),
            totalKobo = totalKobo(EXTRA_STORE_SURCHARGE_KOBO)
        )
    }

    fun placeOrder(): Order? {
        val currentCart = cartState.value
        if (currentCart.items.isEmpty()) return null
        val dropOff = currentCart.dropOffAddress ?: return null
        if (currentCart.hasPendingSurchargeAcceptance) return null

        val order = Order(
            id = UUID.randomUUID().toString(),
            status = OrderStatus.SUBMITTED,
            items = currentCart.items,
            storeSequence = currentCart.storeSequence,
            deliveryMethod = currentCart.deliveryMethod,
            dropOffAddress = dropOff,
            subtotalKobo = currentCart.subtotalKobo,
            deliveryFeeKobo = currentCart.deliveryFeeKobo(),
            surchargeKobo = currentCart.surchargeKobo(EXTRA_STORE_SURCHARGE_KOBO),
            totalKobo = currentCart.totalKobo(EXTRA_STORE_SURCHARGE_KOBO),
            createdAt = Instant.now(),
            extraStoreIds = currentCart.extraStoreIds
        )

        ordersState.update { listOf(order) + it }
        createJobTicketFor(order)
        clearCart()
        return order
    }

    private fun createJobTicketFor(order: Order) {
        val pickupStops = order.storeSequence.mapNotNull { storeId ->
            val store = storeIndex[storeId] ?: return@mapNotNull null
            val readyTime = 10 + (order.items.count { it.product.storeId == storeId } * 3)
            PickupStop(store = store, readyInMinutes = readyTime)
        }
        val ticket = JobTicket(
            id = "JOB-${order.id.take(8)}",
            orderId = order.id,
            pickups = pickupStops,
            dropOff = order.dropOffAddress,
            method = order.deliveryMethod,
            payoutKobo = order.deliveryFeeKobo + order.surchargeKobo,
            state = JobState.UNCLAIMED
        )
        jobTicketsState.update { it + ticket }
    }

    fun simulateJobClaim(jobId: String, transporterId: String) {
        jobTicketsState.update { tickets ->
            tickets.map { ticket ->
                if (ticket.id == jobId) {
                    ticket.copy(
                        state = JobState.CLAIMED,
                        assignedTransporterId = transporterId,
                        lastLocationPingAt = Instant.now()
                    )
                } else {
                    ticket
                }
            }
        }
    }

    fun advanceJob(jobId: String) {
        jobTicketsState.update { tickets ->
            tickets.map { ticket ->
                if (ticket.id == jobId) {
                    ticket.copy(
                        state = when (ticket.state) {
                            JobState.UNCLAIMED -> JobState.CLAIMED
                            JobState.CLAIMED -> JobState.EN_ROUTE
                            JobState.EN_ROUTE -> JobState.DELIVERED
                            JobState.DELIVERED -> JobState.DELIVERED
                        },
                        lastLocationPingAt = Instant.now()
                    )
                } else {
                    ticket
                }
            }
        }
    }

    data class CartTotals(
        val subtotalKobo: Long,
        val deliveryFeeKobo: Long,
        val surchargeKobo: Long,
        val totalKobo: Long
    ) {
        val subtotalNaira: Double get() = subtotalKobo / 100.0
        val deliveryFeeNaira: Double get() = deliveryFeeKobo / 100.0
        val surchargeNaira: Double get() = surchargeKobo / 100.0
        val totalNaira: Double get() = totalKobo / 100.0
    }
}
