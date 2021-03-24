package com.stringconcat.dev.course.app.listeners

import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrder
import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrderRequest
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderCookedDomainEvent
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.ddd.order.usecase.menu.MealExtractor
import com.stringconcat.ddd.order.usecase.order.CustomerOrderExtractor
import com.stringconcat.dev.course.app.event.DomainEventListener

class SendOrderToDeliveryAfterCookedRule(
    private val customerOrderExtractor: CustomerOrderExtractor,
    private val mealExtractor: MealExtractor,
    private val createDeliveryOrder: CreateDeliveryOrder
) : DomainEventListener<KitchenOrderCookedDomainEvent> {

    override fun eventType() = KitchenOrderCookedDomainEvent::class

    override fun handle(event: KitchenOrderCookedDomainEvent) {
        val order = customerOrderExtractor.getById(CustomerOrderId(event.orderId.value))
        checkNotNull(order) {
            "Customer order #${event.orderId} not found"
        }

        val itemData = order.orderItems.map {
            val meal = mealExtractor.getById(it.mealId)
            checkNotNull(meal) {
                "Meal #${it.mealId} not found"
            }

            CreateDeliveryOrderRequest.OrderItemData(
                mealName = meal.name.value,
                count = it.count.value
            )
        }

        val addressData = CreateDeliveryOrderRequest.AddressData(
            street = order.address.street,
            building = order.address.building
        )

        val request = CreateDeliveryOrderRequest(id = order.id.value, items = itemData, address = addressData)

        createDeliveryOrder.execute(request).mapLeft {
            error("Cannot create order #${order.id} for kitchen: $it")
        }
    }
}