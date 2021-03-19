package com.stringconcat.dev.course.app.telnet.order.menu

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.order.domain.cart.CustomerId
import com.stringconcat.ddd.order.domain.menu.MealId
import com.stringconcat.ddd.order.usecase.cart.AddMealToCart
import com.stringconcat.ddd.order.usecase.cart.AddMealToCartUseCaseError
import com.stringconcat.dev.course.app.customerId
import com.stringconcat.dev.course.app.mealId
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class AddMealToCartCommandTest {

    @Test
    fun `meal successfully added`() {

        val mealId = mealId()
        val customerId = customerId()

        val useCase = TestAddMealToCart(Unit.right())

        val command = AddMealToCartCommand(useCase)
        val result = command.execute(
            line = "add ${mealId.value}",
            sessionParameters = emptyMap(),
            sessionId = UUID.fromString(customerId.value)
        )

        result shouldBe "OK"
        useCase.mealId shouldBe mealId
        useCase.customerId shouldBe customerId
    }

    @Test
    fun `meal not found`() {

        val mealId = mealId()
        val customerId = customerId()

        val useCase = TestAddMealToCart(AddMealToCartUseCaseError.MealNotFound.left())
        val command = AddMealToCartCommand(useCase)

        val result = command.execute(
            line = "add ${mealId.value}",
            sessionParameters = emptyMap(),
            sessionId = UUID.fromString(customerId.value)
        )

        result shouldBe "Meal not found"
        useCase.mealId shouldBe mealId
        useCase.customerId shouldBe customerId
    }

    @Test
    fun `invalid parameter`() {
        val useCase = TestAddMealToCart(Unit.right())

        val command = AddMealToCartCommand(useCase)
        val result = command.execute(
            line = "add gggg",
            sessionParameters = emptyMap(),
            sessionId = UUID.randomUUID()
        )

        result shouldBe "Invalid argument"
        useCase.verifyZeroInteractions()
    }

    @Test
    fun `cart has max limit error`() {
        val mealId = mealId()
        val customerId = customerId()

        val useCase = TestAddMealToCart(AddMealToCartUseCaseError.MaxLimitMeal.left())

        val command = AddMealToCartCommand(useCase)
        val result = command.execute(
            line = "add ${mealId.value}",
            sessionParameters = emptyMap(),
            sessionId = UUID.fromString(customerId.value)
        )

        result shouldBe "Maximum number of dishes reached"
        useCase.mealId shouldBe mealId
        useCase.customerId shouldBe customerId
    }

    class TestAddMealToCart(private val response: Either<AddMealToCartUseCaseError, Unit>) : AddMealToCart {

        lateinit var mealId: MealId
        lateinit var customerId: CustomerId

        override fun execute(forCustomer: CustomerId, mealId: MealId): Either<AddMealToCartUseCaseError, Unit> {
            this.customerId = forCustomer
            this.mealId = mealId
            return response
        }

        fun verifyZeroInteractions() {
            ::mealId.isInitialized.shouldBeFalse()
            ::customerId.isInitialized.shouldBeFalse()
        }
    }
}