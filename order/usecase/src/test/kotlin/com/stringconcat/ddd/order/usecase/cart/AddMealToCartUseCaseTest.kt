package com.stringconcat.ddd.order.usecase.cart

import com.stringconcat.ddd.order.domain.cart.CartIdGenerator
import com.stringconcat.ddd.order.domain.cart.CustomerId
import com.stringconcat.ddd.order.domain.cart.MealCountLimitRuleImpl
import com.stringconcat.ddd.order.usecase.TestCartExtractor
import com.stringconcat.ddd.order.usecase.TestCartPersister
import com.stringconcat.ddd.order.usecase.TestMealExtractor
import com.stringconcat.ddd.order.usecase.cart
import com.stringconcat.ddd.order.usecase.cartId
import com.stringconcat.ddd.order.usecase.count
import com.stringconcat.ddd.order.usecase.customerId
import com.stringconcat.ddd.order.usecase.meal
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import java.util.UUID

internal class AddMealToCartUseCaseTest {

    @Test
    fun `cart doesn't exist - successfully added`() {

        val meal = meal()
        val cartPersister = TestCartPersister()
        val cartExtractor = TestCartExtractor()
        val mealExtractor = TestMealExtractor().apply {
            this[meal.id] = meal
        }

        val useCase = AddMealToCartUseCase(
            cartExtractor = cartExtractor,
            idGenerator = TestCartIdGenerator,
            mealExtractor = mealExtractor,
            cartPersister = cartPersister,
            mealCountLimitRule = MealCountLimitRuleImpl(10)
        )

        val customerId = customerId()
        val result = useCase.execute(customerId, meal.id)
        result.shouldBeRight()
        cartPersister shouldContainKey customerId
        val cart = cartPersister[customerId]

        cart.shouldNotBeNull()
        cart.id shouldBe TestCartIdGenerator.id
        cart.forCustomer shouldBe customerId
        cart.meals() shouldContainExactly mapOf(meal.id to count(1))
    }

    @Test
    fun `cart exists - successfully added`() {

        val meal = meal()
        val customerId = customerId()
        val existingCart = cart(customerId = customerId)

        val cartPersister = TestCartPersister()
        val mealExtractor = TestMealExtractor().apply {
            this[meal.id] = meal
        }
        val cartExtractor = TestCartExtractor().apply {
            this[customerId] = existingCart
        }

        val useCase = AddMealToCartUseCase(
            cartExtractor = cartExtractor,
            idGenerator = TestCartIdGenerator,
            mealExtractor = mealExtractor,
            cartPersister = cartPersister,
            mealCountLimitRule = MealCountLimitRuleImpl(10)
        )

        val result = useCase.execute(customerId, meal.id)
        result.shouldBeRight()
        cartPersister shouldContainKey customerId
        val cart = cartPersister[customerId]

        cart.shouldNotBeNull()
        cart shouldBeSameInstanceAs existingCart
        cart.meals() shouldContainExactly mapOf(meal.id to count(1))
    }

    @Test
    fun `meal not found`() {

        val meal = meal()
        val cartPersister = TestCartPersister()
        val cartExtractor = TestCartExtractor()
        val mealExtractor = TestMealExtractor()

        val useCase = AddMealToCartUseCase(
            cartExtractor = cartExtractor,
            idGenerator = TestCartIdGenerator,
            mealExtractor = mealExtractor,
            cartPersister = cartPersister,
            mealCountLimitRule = MealCountLimitRuleImpl(10)
        )

        val result = useCase.execute(CustomerId(UUID.randomUUID().toString()), meal.id)
        result shouldBeLeft AddMealToCartUseCaseError.MealNotFound
        cartPersister.shouldBeEmpty()
    }

    @Test
    fun `limit meal for add to cart`() {
        val meal1 = meal()
        val meal2 = meal()
        val meal3 = meal()

        val customerId = customerId()
        val mealCountLimitRule = MealCountLimitRuleImpl(2)
        val existingCart = cart(customerId = customerId, mealCountLimitRule = mealCountLimitRule)

        val cartPersister = TestCartPersister()
        val mealExtractor = TestMealExtractor().apply {
            this[meal1.id] = meal1
            this[meal2.id] = meal2
            this[meal3.id] = meal3
        }
        val cartExtractor = TestCartExtractor().apply {
            this[customerId] = existingCart
        }

        val useCase = AddMealToCartUseCase(
            mealExtractor = mealExtractor,
            cartPersister = cartPersister,
            cartExtractor = cartExtractor,
            idGenerator = TestCartIdGenerator,
            mealCountLimitRule = mealCountLimitRule
        )

        useCase.execute(customerId, meal1.id)
        useCase.execute(customerId, meal2.id)
        val result = useCase.execute(customerId, meal3.id)
        result shouldBeLeft AddMealToCartUseCaseError.MaxLimitMeal
        cartPersister[customerId]?.meals()?.size shouldBe 2
    }

    object TestCartIdGenerator : CartIdGenerator {
        val id = cartId()
        override fun generate() = id
    }
}