package com.stringconcat.integration.crm

import com.stringconcat.ddd.order.domain.menu.Price
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.ddd.order.usecase.order.CrmProvider
import org.slf4j.LoggerFactory

class CrmSenderProvider : CrmProvider {
    companion object {
        val logger = LoggerFactory.getLogger(CrmSenderProvider::class.java)
    }

    override fun send(orderId: CustomerOrderId, price: Price) {
        logger.info("Send to crm order with id = ${orderId.value} money ${price.value}")
    }
}