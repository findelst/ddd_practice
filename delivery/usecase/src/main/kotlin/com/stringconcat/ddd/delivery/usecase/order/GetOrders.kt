package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.domain.order.OrderItem

interface GetOrders {

    fun execute(): List<DeliveryOrderInfo>
}

data class DeliveryOrderInfo(
    val id: DeliveryOrderId,
    val delivered: Boolean,
    val meals: List<OrderItem>
)