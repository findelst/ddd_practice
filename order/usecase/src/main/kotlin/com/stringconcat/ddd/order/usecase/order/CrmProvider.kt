package com.stringconcat.ddd.order.usecase.order

import com.stringconcat.ddd.order.domain.menu.Price
import com.stringconcat.ddd.order.domain.order.CustomerOrderId

interface CrmProvider {
    fun send(orderId: CustomerOrderId, price: Price)
}