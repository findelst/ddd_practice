package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either

interface CreateDeliveryOrder {
    fun execute(requestDelivery: CreateDeliveryOrderRequest): Either<CreateDeliveryOrderUseCaseError, Unit>
}

data class CreateDeliveryOrderRequest(val id: Long, val items: List<OrderItemData>, val address: AddressData) {
    data class OrderItemData(val mealName: String, val count: Int)
    data class AddressData(val street: String, val building: Int)
}

sealed class CreateDeliveryOrderUseCaseError {
    data class InvalidCount(val message: String) : CreateDeliveryOrderUseCaseError()
    data class InvalidMealName(val message: String) : CreateDeliveryOrderUseCaseError()
    data class InvalidAddressStreet(val message: String) : CreateDeliveryOrderUseCaseError()
    data class InvalidAddressBuilding(val message: String) : CreateDeliveryOrderUseCaseError()
    object EmptyDeliveryOrder : CreateDeliveryOrderUseCaseError()
}