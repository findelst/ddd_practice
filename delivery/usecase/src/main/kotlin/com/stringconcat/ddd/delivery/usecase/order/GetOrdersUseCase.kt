package com.stringconcat.ddd.delivery.usecase.order

class GetOrdersUseCase(private val orderExtractor: DeliveryOrderExtractor) : GetOrders {

    override fun execute(): List<DeliveryOrderInfo> {
        return orderExtractor.getAll().map {
            DeliveryOrderInfo(
                id = it.id,
                meals = it.meals,
                delivered = it.delivered
            )
        }.toList()
    }
}