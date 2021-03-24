package com.stringconcat.ddd.delivery.persistence.order

import com.stringconcat.ddd.common.types.base.EventPublisher
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderExtractor
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderPersister

class InMemoryDeliveryOrderRepository(private val eventPublisher: EventPublisher) :
    DeliveryOrderExtractor,
    DeliveryOrderPersister {

    internal val storage = LinkedHashMap<DeliveryOrderId, DeliveryOrder>()

    override fun getById(orderId: DeliveryOrderId) = storage[orderId]

    override fun getAll() = storage.values.toList()

    override fun save(order: DeliveryOrder) {
        eventPublisher.publish(order.popEvents())
        storage[order.id] = order
    }
}