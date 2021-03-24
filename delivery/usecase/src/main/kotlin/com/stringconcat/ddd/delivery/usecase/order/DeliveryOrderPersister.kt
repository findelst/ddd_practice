package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder

interface DeliveryOrderPersister {
    fun save(order: DeliveryOrder)
}