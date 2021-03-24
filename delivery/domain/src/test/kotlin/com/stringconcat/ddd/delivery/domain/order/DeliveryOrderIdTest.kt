package com.stringconcat.ddd.delivery.domain.order

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import kotlin.random.Random

class DeliveryOrderIdTest {

    @Test
    fun `check equality`() {
        val id = Random.nextLong()

        val deliveryOrderId1 = DeliveryOrderId(id)
        val deliveryOrderId2 = DeliveryOrderId(id)
        deliveryOrderId1 shouldBe deliveryOrderId2
        deliveryOrderId1 shouldNotBeSameInstanceAs deliveryOrderId2
        deliveryOrderId1.value shouldBe deliveryOrderId2.value
    }
}