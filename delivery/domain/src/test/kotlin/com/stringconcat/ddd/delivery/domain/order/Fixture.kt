package com.stringconcat.ddd.delivery.domain.order

import arrow.core.Either
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
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

fun order(delivered: Boolean) = DeliveryOrderRestorer.restoreOrder(
    id = orderId(),
    orderItems = listOf(orderItem()),
    address = address(),
    delivered = delivered,
    version = version()
)

fun address(street: String = "Moscow City", building: Int = 12): Address {
    val result = Address.from(street, building)
    check(result is Either.Right<Address>)
    return result.b
}