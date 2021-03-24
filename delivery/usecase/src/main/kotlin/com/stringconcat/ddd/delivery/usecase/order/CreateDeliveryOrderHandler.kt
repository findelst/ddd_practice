package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import arrow.core.extensions.either.apply.tupled
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
import com.stringconcat.ddd.common.types.common.CreateAddressError
import com.stringconcat.ddd.common.types.common.NegativeValueError
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.domain.order.EmptyMealNameError
import com.stringconcat.ddd.delivery.domain.order.EmptyOrder
import com.stringconcat.ddd.delivery.domain.order.Meal
import com.stringconcat.ddd.delivery.domain.order.OrderItem

class CreateDeliveryOrderHandler(
    private val extractor: DeliveryOrderExtractor,
    private val persister: DeliveryOrderPersister
) : CreateDeliveryOrder {

    override fun execute(requestDelivery: CreateDeliveryOrderRequest): Either<CreateDeliveryOrderUseCaseError, Unit> {
        val order = extractor.getById(DeliveryOrderId(requestDelivery.id)) // выпоняем дедупликацю
        return if (order != null) {
            Unit.right()
        } else {
            createNewOrder(requestDelivery)
        }
    }

    private fun createNewOrder(request: CreateDeliveryOrderRequest): Either<CreateDeliveryOrderUseCaseError, Unit> {

        val items = request.items.map {
            tupled(
                transform(it.count),
                transform(it.mealName)
            ).map { sourceItem -> OrderItem(sourceItem.b, sourceItem.a) }
        }.map {
            it.mapLeft { e -> return@createNewOrder e.left() }
        }.mapNotNull { it.orNull() }

        val address = Address.from(request.address.street, request.address.building).mapLeft {
            return@createNewOrder when (it) {
                is CreateAddressError.EmptyString -> it.toError().left()
                is CreateAddressError.NonPositiveBuilding -> it.toError().left()
            }
        }.orNull()!!

        return DeliveryOrder.create(id = DeliveryOrderId(request.id), items = items, address = address)
            .mapLeft { it.toError() }
            .map { order ->
                persister.save(order)
            }
    }

    private fun transform(count: Int): Either<CreateDeliveryOrderUseCaseError, Count> {
        return Count.from(count).mapLeft { it.toError() }
    }

    private fun transform(mealName: String): Either<CreateDeliveryOrderUseCaseError, Meal> {
        return Meal.from(mealName).mapLeft { it.toError() }
    }
}

fun NegativeValueError.toError() = CreateDeliveryOrderUseCaseError.InvalidCount("Negative value")
fun EmptyMealNameError.toError() = CreateDeliveryOrderUseCaseError.InvalidMealName("Meal name is empty")
fun CreateAddressError.EmptyString.toError() = CreateDeliveryOrderUseCaseError.InvalidAddressStreet("street is empty")
fun CreateAddressError.NonPositiveBuilding.toError() =
    CreateDeliveryOrderUseCaseError.InvalidAddressBuilding("building is negative number")

fun EmptyOrder.toError() = CreateDeliveryOrderUseCaseError.EmptyDeliveryOrder