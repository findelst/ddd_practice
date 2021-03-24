package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderCreatedDomainEvent
import com.stringconcat.ddd.delivery.domain.order.OrderItem
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CreateDeliveryOrderHandlerTest {

    @Test
    fun `order doesn't exist - order created successfully`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()
        val count = count()

        val itemData = CreateDeliveryOrderRequest.OrderItemData(
            mealName = meal.value,
            count = count.value
        )

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = listOf(itemData),
            address = addressData()
        )

        val result = handler.execute(request)
        result.shouldBeRight()

        val order = persister[orderId]
        order.shouldNotBeNull()
        order.id shouldBe orderId
        order.meals shouldContainExactly listOf(OrderItem(meal, count))
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(orderId))
    }

    @Test
    fun `order exists - order not created`() {

        val existingOrder = order()

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor().apply {
            this[existingOrder.id] = existingOrder
        }

        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val request = CreateDeliveryOrderRequest(
            id = existingOrder.id.value,
            items = emptyList(),
            address = addressData()
        )

        val result = handler.execute(request)
        result.shouldBeRight()

        val order = persister[existingOrder.id]
        order.shouldBeNull()
    }

    @Test
    fun `order is empty`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()
        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = emptyList(),
            address = addressData()
        )

        val result = handler.execute(request)
        result shouldBeLeft CreateDeliveryOrderUseCaseError.EmptyDeliveryOrder
    }

    @Test
    fun `invalid count`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()
        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()

        val itemData = CreateDeliveryOrderRequest.OrderItemData(
            mealName = meal.value,
            count = -1
        )

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = listOf(itemData),
            address = addressData()
        )

        val result = handler.execute(request)
        result shouldBeLeft CreateDeliveryOrderUseCaseError.InvalidCount("Negative value")
    }

    @Test
    fun `invalid meal`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()
        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()
        val count = count()

        val itemData = CreateDeliveryOrderRequest.OrderItemData(
            mealName = "",
            count = count.value
        )

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = listOf(itemData),
            address = addressData()
        )

        val result = handler.execute(request)
        result shouldBeLeft CreateDeliveryOrderUseCaseError.InvalidMealName("Meal name is empty")
    }

    @Test
    fun `invalid address street`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()
        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()
        val count = count()

        val itemData = CreateDeliveryOrderRequest.OrderItemData(
            mealName = meal.value,
            count = count.value
        )

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = listOf(itemData),
            address = addressData("", 12)
        )

        val result = handler.execute(request)
        result shouldBeLeft CreateDeliveryOrderUseCaseError.InvalidAddressStreet("street is empty")
    }

    @Test
    fun `invalid address building`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()
        val handler = CreateDeliveryOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()
        val count = count()

        val itemData = CreateDeliveryOrderRequest.OrderItemData(
            mealName = meal.value,
            count = count.value
        )

        val request = CreateDeliveryOrderRequest(
            id = orderId.value,
            items = listOf(itemData),
            address = addressData("Moscow City", -12)
        )

        val result = handler.execute(request)
        result shouldBeLeft CreateDeliveryOrderUseCaseError.InvalidAddressBuilding("building is negative number")
    }
}