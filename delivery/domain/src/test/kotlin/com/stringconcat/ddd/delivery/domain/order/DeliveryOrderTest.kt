package com.stringconcat.ddd.delivery.domain.order

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DeliveryOrderTest {

    @Test
    fun `create order - success`() {
        val id = orderId()
        val item = orderItem()
        val items = listOf(item)
        val address = address()
        val result = DeliveryOrder.create(id = id, items, address)

        result.shouldBeRight { order ->
            order.id shouldBe id
            order.version.value shouldBe version().value
            order.address shouldBe address()

            order.meals.size shouldBe 1
            val orderItem = order.meals.first()
            orderItem.meal shouldBe item.meal
            orderItem.count shouldBe item.count

            order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(id))
        }
    }

    @Test
    fun `create order - empty items`() {
        val result = DeliveryOrder.create(id = orderId(), emptyList(), address())
        result shouldBeLeft EmptyOrder
    }

    @Test
    fun `order delivered - success`() {
        val order = order(delivered = false)
        order.delivery()
        order.delivered shouldBe true
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCookedDomainEvent(order.id))
    }

    @Test
    fun `order delivered - already delivered`() {
        val order = order(delivered = true)
        order.delivery()
        order.delivered shouldBe true
        order.popEvents().shouldBeEmpty()
    }
}