package com.stringconcat.ddd.order.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.order.domain.order.CustomerOrderId

class PayOrderHandlerWithCrm(
    val payOrder: PayOrder,
    private val customerOrderExtractor: CustomerOrderExtractor,
    val crmProvider: CrmProvider
) : PayOrder {

    override fun execute(orderId: CustomerOrderId): Either<PayOrderHandlerError, Unit> {
        val executeResult = payOrder.execute(orderId)
        when (executeResult) {
            is Either.Right -> {
                val order = customerOrderExtractor.getById(orderId)!!
                crmProvider.send(order.id, order.totalPrice())
            }
        }

        return executeResult
    }
}