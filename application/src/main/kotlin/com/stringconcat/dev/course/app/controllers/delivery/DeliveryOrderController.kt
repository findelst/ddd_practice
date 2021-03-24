package com.stringconcat.dev.course.app.controllers.delivery

import arrow.core.Either
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrder
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderInfo
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderUseCaseError
import com.stringconcat.ddd.delivery.usecase.order.GetOrders
import com.stringconcat.dev.course.app.controllers.URLs
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class DeliveryOrderController(
    private val getDeliveryOrdersUseCase: GetOrders,
    private val deliveryOrder: DeliveryOrder
) {

    @GetMapping(URLs.delivery_orders)
    fun orders(): List<DeliveryOrderInfo> {
        return getDeliveryOrdersUseCase.execute()
    }

    @PostMapping(URLs.delivery_courier_order)
    fun confirm(@RequestParam orderId: Long): Either<DeliveryOrderUseCaseError, Unit> {
        return deliveryOrder.execute(DeliveryOrderId(orderId))
    }
}