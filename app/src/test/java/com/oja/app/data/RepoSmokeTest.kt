package com.oja.app.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepoSmokeTest {

    @Before
    fun setUp() {
        Repo.resetForTesting()
    }

    @After
    fun tearDown() {
        Repo.resetForTesting()
    }

    @Test
    fun cartStateGroupsByStoreAndTracksSubtotal() {
        val first = Repo.products[0]
        val second = Repo.products[1]
        val cartState = CartState(
            items = listOf(
                CartItem(product = first, qty = 1),
                CartItem(product = second, qty = 2)
            )
        )

        val groups = cartState.groupedByStore
        assertEquals(2, groups.size)
        assertEquals(1, groups[first.storeId]?.size)
        assertEquals(1, groups[second.storeId]?.size)
        assertEquals(2, groups[second.storeId]?.first()?.qty)

        val expectedSubtotal = first.price * 1 + second.price * 2
        assertEquals(expectedSubtotal, cartState.subtotal)
    }

    @Test
    fun createOrderAddsJobAndClearsCart() {
        Repo.addToCart(Repo.products[0])
        Repo.addToCart(Repo.products[1])

        val beforeJobs = Repo.jobs.value.size
        val order = Repo.createOrder(DeliveryMethod.BIKE)

        assertTrue(order.storeIds.containsAll(listOf("s1", "s2")))
        val expectedTotal = Repo.products[0].price + Repo.products[1].price + 700
        assertEquals(expectedTotal, order.total)
        assertTrue("Cart should be cleared after order", Repo.cart.value.items.isEmpty())

        val afterJobs = Repo.jobs.value
        assertEquals(beforeJobs + 1, afterJobs.size)
        assertTrue(afterJobs.any { it.orderId == order.id })
    }
}
