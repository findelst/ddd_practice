package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderRestorer
import com.stringconcat.ddd.delivery.domain.order.Meal
import com.stringconcat.ddd.delivery.domain.order.OrderItem
import kotlin.random.Random

fun orderId() = DeliveryOrderId(Random.nextLong())

fun version() = Version.new()

fun count(value: Int = Random.nextInt(20, 5000)): Count {
    val result = Count.from(value)
    check(result is Either.Right<Count>)
    return result.b
}

fun meal(): Meal {
    val result = Meal.from("Meal #${Random.nextInt()}")
    check(result is Either.Right<Meal>)
    return result.b
}

fun orderItem(): OrderItem {
    return OrderItem(
        meal = meal(),
        count = count()
    )
}

fun order(delivered: Boolean = false) = DeliveryOrderRestorer.restoreOrder(
    id = orderId(),
    orderItems = listOf(orderItem()),
    address = address(),
    delivered = delivered,
    version = version()
)

class TestDeliveryOrderPersister : DeliveryOrderPersister, HashMap<DeliveryOrderId, DeliveryOrder>() {
    override fun save(order: DeliveryOrder) {
        this[order.id] = order
    }
}

class TestDeliveryOrderExtractor : DeliveryOrderExtractor, HashMap<DeliveryOrderId, DeliveryOrder>() {
    override fun getById(orderId: DeliveryOrderId) = this[orderId]

    override fun getAll() = values.toList()
}

fun address(street: String = "Moscow City", building: Int = 12): Address {
    val result = Address.from(street, building)
    check(result is Either.Right<Address>)
    return result.b
}

fun addressData(street: String = "Moscow City", building: Int = 12) =
    CreateDeliveryOrderRequest.AddressData(street, building)