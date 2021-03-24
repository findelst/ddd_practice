package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

class DeliveryOrderUseCase(
    private val extractor: DeliveryOrderExtractor,
    private val persister: DeliveryOrderPersister
) : DeliveryOrder {
    override fun execute(orderId: DeliveryOrderId): Either<DeliveryOrderUseCaseError, Unit> {
        return extractor.getById(orderId)
            .rightIfNotNull { DeliveryOrderUseCaseError.OrderNotFound }
            .map { deliveryOrder ->
                deliveryOrder.delivery()
                persister.save(deliveryOrder)
            }
    }
}