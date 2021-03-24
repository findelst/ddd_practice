package com.stringconcat.ddd.delivery.domain.order

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.common.types.base.AggregateRoot
import com.stringconcat.ddd.common.types.base.ValueObject
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
import com.stringconcat.ddd.common.types.error.BusinessError

data class DeliveryOrderId(val value: Long)

class DeliveryOrder internal constructor(
    id: DeliveryOrderId,
    val meals: List<OrderItem>,
    val address: Address,
    version: Version
) : AggregateRoot<DeliveryOrderId>(id, version) {

    var delivered: Boolean = false
        internal set

    fun delivery() {
        if (!delivered) {
            delivered = true
            addEvent(DeliveryOrderCookedDomainEvent(id))
        }
    }

    companion object {
        fun create(id: DeliveryOrderId, items: List<OrderItem>, address: Address): Either<EmptyOrder, DeliveryOrder> {
            return when {
                items.isEmpty() -> EmptyOrder.left()
                else -> {
                    DeliveryOrder(id, items, address, Version.new()).apply {
                        addEvent(DeliveryOrderCreatedDomainEvent(id))
                    }.right()
                }
            }
        }
    }
}

object EmptyOrder : BusinessError

data class OrderItem(
    val meal: Meal,
    val count: Count
) : ValueObject