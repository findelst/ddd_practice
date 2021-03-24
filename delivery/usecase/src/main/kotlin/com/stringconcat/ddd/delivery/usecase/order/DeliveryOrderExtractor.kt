package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

interface DeliveryOrderExtractor {

    fun getById(orderId: DeliveryOrderId): DeliveryOrder?

    fun getAll(): List<DeliveryOrder>
}