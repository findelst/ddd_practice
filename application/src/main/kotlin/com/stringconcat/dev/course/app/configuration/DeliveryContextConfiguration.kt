package com.stringconcat.dev.course.app.configuration

import com.stringconcat.ddd.common.types.base.EventPublisher
import com.stringconcat.ddd.delivery.persistence.order.InMemoryDeliveryOrderRepository
import com.stringconcat.ddd.delivery.usecase.order.CreateDeliveryOrderHandler
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderExtractor
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderPersister
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderUseCase
import com.stringconcat.ddd.delivery.usecase.order.GetOrdersUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("TooManyFunctions")
class DeliveryContextConfiguration {

    @Bean
    fun deliveryOrderRepository(eventPublisher: EventPublisher) = InMemoryDeliveryOrderRepository(eventPublisher)

    @Bean
    fun deliveryOrderUseCase(
        deliverOrderExtractor: DeliveryOrderExtractor,
        deliveryOrderPersister: DeliveryOrderPersister
    ) = DeliveryOrderUseCase(deliverOrderExtractor, deliveryOrderPersister)

    @Bean
    fun createDeliveryOrderHandler(
        deliveryOrderExtractor: DeliveryOrderExtractor,
        deliveryOrderPersister: DeliveryOrderPersister
    ) = CreateDeliveryOrderHandler(deliveryOrderExtractor, deliveryOrderPersister)

    @Bean
    fun getDeliveryOrdersUseCase(deliveryOrderExtractor: DeliveryOrderExtractor) =
        GetOrdersUseCase(deliveryOrderExtractor)
}