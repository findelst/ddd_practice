package com.stringconcat.ddd.order.usecase.cart

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.rightIfNotNull
import com.stringconcat.ddd.order.domain.cart.Cart
import com.stringconcat.ddd.order.domain.cart.CartIdGenerator
import com.stringconcat.ddd.order.domain.cart.CustomerId
import com.stringconcat.ddd.order.domain.cart.MealCountLimitRule
import com.stringconcat.ddd.order.domain.menu.MealId
import com.stringconcat.ddd.order.usecase.menu.MealExtractor

class AddMealToCartUseCase(
    private val cartExtractor: CartExtractor,
    private val idGenerator: CartIdGenerator,
    private val mealExtractor: MealExtractor,
    private val cartPersister: CartPersister,
    private val mealCountLimitRule: MealCountLimitRule
) : AddMealToCart {
    override fun execute(
        forCustomer: CustomerId,
        mealId: MealId
    ): Either<AddMealToCartUseCaseError, Unit> =
        mealExtractor
            .getById(mealId)
            .rightIfNotNull { AddMealToCartUseCaseError.MealNotFound }
            .flatMap {
                val cart = getOrCreateCart(forCustomer)
                cart.addMeal(it).mapLeft {
                    AddMealToCartUseCaseError.MaxLimitMeal
                }.map { cart }
            }.map {
                cartPersister.save(it)
            }

    private fun getOrCreateCart(forCustomer: CustomerId): Cart {
        return cartExtractor.getCart(forCustomer)
            ?: Cart.create(
                idGenerator = idGenerator,
                forCustomer = forCustomer,
                mealCountLimitRule = mealCountLimitRule
            )
    }
}
