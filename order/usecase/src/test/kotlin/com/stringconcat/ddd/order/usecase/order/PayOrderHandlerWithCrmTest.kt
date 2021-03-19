package com.stringconcat.ddd.order.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.order.usecase.TestCrmProvider
import com.stringconcat.ddd.order.usecase.TestCustomerOrderExtractor
import com.stringconcat.ddd.order.usecase.orderNotReadyForPay
import com.stringconcat.ddd.order.usecase.orderReadyForPay
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class PayOrderHandlerWithCrmTest {

    @Test
    fun `send crm - successfully send`() {
        val order = orderReadyForPay()
        val extractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val payOrder = mockk<PayOrder>()
        every { payOrder.execute(orderId = order.id) } returns Either.right(Unit)

        val crmProvider = TestCrmProvider()
        val handlerWithCrm = PayOrderHandlerWithCrm(payOrder, extractor, crmProvider)
        val result = handlerWithCrm.execute(orderId = order.id)

        result.shouldBeRight()

        crmProvider.orders.size shouldBe 1
        crmProvider.orders[order.id] shouldBe order.totalPrice()
    }

    @Test
    fun `not send crm when not successfully payed`() {
        val order = orderNotReadyForPay()
        val extractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val payOrder = mockk<PayOrder>()
        every { payOrder.execute(orderId = order.id) } returns Either.left(PayOrderHandlerError.InvalidOrderState)

        val crmProvider = TestCrmProvider()

        val handlerWithCrm = PayOrderHandlerWithCrm(payOrder, extractor, crmProvider)
        val result = handlerWithCrm.execute(orderId = order.id)

        result.shouldBeLeft(PayOrderHandlerError.InvalidOrderState)
        crmProvider.orders.size shouldBe 0
    }
}