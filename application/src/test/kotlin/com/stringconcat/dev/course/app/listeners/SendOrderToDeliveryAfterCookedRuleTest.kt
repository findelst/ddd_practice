package com.stringconcat.dev.course.app.listeners

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrder
import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrderRequest
import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrderUseCaseError
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderCookedDomainEvent
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderId
import com.stringconcat.ddd.order.domain.order.OrderItem
import com.stringconcat.dev.course.app.TestCustomerOrderExtractor
import com.stringconcat.dev.course.app.TestMealExtractor
import com.stringconcat.dev.course.app.count
import com.stringconcat.dev.course.app.customerOrder
import com.stringconcat.dev.course.app.kitchenOrderId
import com.stringconcat.dev.course.app.meal
import com.stringconcat.dev.course.app.price
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SendOrderToDeliveryAfterCookedRuleTest {

    @Test
    fun `order successfully sent to delivery`() {

        val meal = meal()
        val price = price()
        val count = count()
        val order = customerOrder(orderItems = setOf(OrderItem(meal.id, price, count)))

        val orderExtractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val mealExtractor = TestMealExtractor().apply {
            this[meal.id] = meal
        }

        val useCase = TestDeliveryCreateDeliveryOrder(Unit.right())

        val rule = SendOrderToDeliveryAfterCookedRule(orderExtractor, mealExtractor, useCase)

        val event = KitchenOrderCookedDomainEvent(KitchenOrderId(order.id.value))
        rule.handle(event)

        useCase.request.id shouldBe order.id.value
        useCase.request.items shouldContainExactly listOf(
            CreateDeliveryOrderRequest.OrderItemData(
                meal.name.value,
                count.value
            )
        )
    }

    @Test
    fun `order not found`() {
        val orderExtractor = TestCustomerOrderExtractor()

        val mealExtractor = TestMealExtractor()

        val useCase = TestDeliveryCreateDeliveryOrder(Unit.right())

        val rule = SendOrderToDeliveryAfterCookedRule(orderExtractor, mealExtractor, useCase)

        val event = KitchenOrderCookedDomainEvent(kitchenOrderId())

        shouldThrow<IllegalStateException> {
            rule.handle(event)
        }

        useCase.verifyZeroInteraction()
    }

    @Test
    fun `meal not found`() {

        val meal = meal()
        val price = price()
        val count = count()
        val order = customerOrder(orderItems = setOf(OrderItem(meal.id, price, count)))

        val orderExtractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val mealExtractor = TestMealExtractor()

        val useCase = TestDeliveryCreateDeliveryOrder(Unit.right())

        val rule = SendOrderToDeliveryAfterCookedRule(orderExtractor, mealExtractor, useCase)

        val event = KitchenOrderCookedDomainEvent(KitchenOrderId(order.id.value))

        shouldThrow<IllegalStateException> {
            rule.handle(event)
        }

        useCase.verifyZeroInteraction()
    }

    @Test
    fun `order creation error`() {

        val meal = meal()
        val price = price()
        val count = count()
        val order = customerOrder(orderItems = setOf(OrderItem(meal.id, price, count)))

        val orderExtractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val mealExtractor = TestMealExtractor().apply {
            this[meal.id] = meal
        }

        val useCase = TestDeliveryCreateDeliveryOrder(CreateDeliveryOrderUseCaseError.EmptyDeliveryOrder.left())

        val rule = SendOrderToDeliveryAfterCookedRule(orderExtractor, mealExtractor, useCase)

        val event = KitchenOrderCookedDomainEvent(KitchenOrderId(order.id.value))

        shouldThrow<IllegalStateException> {
            rule.handle(event)
        }

        useCase.request.id shouldBe order.id.value
        useCase.request.items shouldContainExactly listOf(
            CreateDeliveryOrderRequest.OrderItemData(
                meal.name.value,
                count.value
            )
        )
    }

    private class TestDeliveryCreateDeliveryOrder(val response: Either<CreateDeliveryOrderUseCaseError, Unit>) :
        CreateDeliveryOrder {

        lateinit var request: CreateDeliveryOrderRequest

        override fun execute(requestDelivery: CreateDeliveryOrderRequest):
            Either<CreateDeliveryOrderUseCaseError, Unit> {
            this.request = requestDelivery
            return response
        }

        fun verifyZeroInteraction() {
            ::request.isInitialized.shouldBeFalse()
        }
    }
}