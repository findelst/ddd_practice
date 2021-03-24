package com.stringconcat.ddd.delivery.domain.order

import com.stringconcat.ddd.common.types.base.DomainEvent

data class DeliveryOrderCreatedDomainEvent(val deliveryOrderId: DeliveryOrderId) : DomainEvent()
data class DeliveryOrderCookedDomainEvent(val deliveryOrderId: DeliveryOrderId) : DomainEvent()