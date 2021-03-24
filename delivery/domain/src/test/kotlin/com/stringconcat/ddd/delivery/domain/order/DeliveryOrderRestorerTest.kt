package com.stringconcat.ddd.delivery.domain.order

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DeliveryOrderRestorerTest {

    @Test
    fun `restore order - success`() {
        val id = orderId()
        val item = orderItem()
        val items = listOf(item)
        val address = address()
        val delivered = true
        val version = version()

        val order = DeliveryOrderRestorer.restoreOrder(
            id = id,
            orderItems = items,
            address = address,
            delivered = delivered,
            version = version
        )

        order.id shouldBe id
        order.address shouldBe address()
        order.delivered shouldBe delivered
        order.version shouldBe version

        order.meals.size shouldBe 1
        val orderItem = order.meals.first()
        orderItem.count shouldBe item.count

        order.popEvents().shouldBeEmpty()
    }
}