package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

interface DeliveryOrder {
    fun execute(orderId: DeliveryOrderId): Either<DeliveryOrderUseCaseError, Unit>
}

sealed class DeliveryOrderUseCaseError(val message: String) {
    object OrderNotFound : DeliveryOrderUseCaseError("Order not found")
}