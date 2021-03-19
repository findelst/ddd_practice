package com.stringconcat.ddd.order.domain.cart

interface MealCountLimitRule {
    fun check(cart: Cart): Boolean
}

class MealCountLimitRuleImpl(val maxLimit: Int) : MealCountLimitRule {
    override fun check(cart: Cart): Boolean {
        return cart.meals().map { it.value.value }.sum() >= maxLimit
    }
}
