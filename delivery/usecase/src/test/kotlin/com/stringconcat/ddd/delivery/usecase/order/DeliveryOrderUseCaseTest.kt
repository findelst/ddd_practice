package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderCookedDomainEvent
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliveryOrderUseCaseTest {

    @Test
    fun `successfully complete`() {

        val order = order()
        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor().apply {
            this[order.id] = order
        }

        val useCase = DeliveryOrderUseCase(extractor, persister)
        val result = useCase.execute(order.id)
        result.shouldBeRight()

        val savedOrder = persister[order.id]
        savedOrder shouldBe order
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCookedDomainEvent(order.id))
    }

    @Test
    fun `order not found`() {
        val order = order()
        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val useCase = DeliveryOrderUseCase(extractor, persister)
        val result = useCase.execute(order.id)
        result shouldBeLeft DeliveryOrderUseCaseError.OrderNotFound
    }
}