package com.stringconcat.ddd.kitchen.persistence.order.com.stringconcat.ddd.delivery.persistence.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderCookedDomainEvent
import com.stringconcat.ddd.delivery.persistence.order.InMemoryDeliveryOrderRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test

internal class InMemoryKitchenOrderRepositoryTest {

    @Test
    fun `saving order - order doesn't exist`() {

        val eventPublisher = TestEventPublisher()
        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        val order = orderWithEvents()

        repository.save(order)

        val storedOrder = repository.storage[order.id]
        storedOrder shouldBeSameInstanceAs order
        eventPublisher.storage.shouldHaveSize(1)

        val event = eventPublisher.storage.first()
        event.shouldBeInstanceOf<DeliveryOrderCookedDomainEvent>()
        event.deliveryOrderId shouldBe order.id
    }

    @Test
    fun `saving order - order exists`() {

        val eventPublisher = TestEventPublisher()
        val id = orderId()
        val existingOrder = order(id = id)

        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        repository.storage[existingOrder.id] = existingOrder

        val updatedOrder = orderWithEvents(id)
        repository.save(updatedOrder)

        val event = eventPublisher.storage.first()
        event.shouldBeInstanceOf<DeliveryOrderCookedDomainEvent>()
        event.deliveryOrderId shouldBe updatedOrder.id
    }

    @Test
    fun `get by id - order exists`() {

        val eventPublisher = TestEventPublisher()
        val existingOrder = order()

        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        repository.storage[existingOrder.id] = existingOrder

        val order = repository.getById(existingOrder.id)
        order shouldBeSameInstanceAs existingOrder
    }

    @Test
    fun `get by id - order doesn't exist`() {

        val eventPublisher = TestEventPublisher()
        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        val order = repository.getById(orderId())
        order.shouldBeNull()
    }

    @Test
    fun `get all - storage is empty`() {
        val eventPublisher = TestEventPublisher()
        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        val result = repository.getAll()
        result.shouldBeEmpty()
    }

    @Test
    fun `get all - storage is not empty`() {
        val order = order()
        val eventPublisher = TestEventPublisher()
        val repository = InMemoryDeliveryOrderRepository(eventPublisher)
        repository.storage[order.id] = order

        val result = repository.getAll()
        result shouldContainExactly listOf(order)
    }
}