package com.stringconcat.ddd.delivery.usecase.order

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class GetOrdersUseCaseTest {

    @Test
    fun `storage is empty`() {

        val extractor = TestDeliveryOrderExtractor()
        val useCase = GetOrdersUseCase(extractor)
        val result = useCase.execute()
        result.shouldBeEmpty()
    }

    @Test
    fun `storage is not empty`() {
        val order = order()
        val extractor = TestDeliveryOrderExtractor().apply {
            this[order.id] = order
        }

        val useCase = GetOrdersUseCase(extractor)
        val result = useCase.execute()
        result shouldContainExactly listOf(
            DeliveryOrderInfo(
                id = order.id,
                meals = order.meals,
                delivered = order.delivered
            )
        )
        order.meals.shouldNotBeEmpty()
    }
}