package com.stringconcat.ddd.order.usecase.cart

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.stringconcat.ddd.order.domain.cart.CustomerId

class RemoveCartHandler(
    private val cartExtractor: CartExtractor,
    private val cartRemover: CartRemover
) {

    fun removeCart(forCustomer: String): Either<RemoveCartHandlerError, Unit> {
        return cartExtractor.getCart(CustomerId(forCustomer)).rightIfNotNull {
            RemoveCartHandlerError.CartNotFound
        }.map {
            // тут можно не делать никаких методов в самой коризине, потому что
            // корзина никому не интересна с точки зрения процессов
            cartRemover.deleteCart(it)
        }
    }
}

sealed class RemoveCartHandlerError {
    object CartNotFound : RemoveCartHandlerError()
}