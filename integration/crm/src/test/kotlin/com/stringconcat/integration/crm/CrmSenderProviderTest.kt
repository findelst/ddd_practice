package com.stringconcat.integration.crm

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.integration.configuration.MemoryAppender
import com.stringconcat.integration.price
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrmSenderProviderTest {

    lateinit var memoryAppender: MemoryAppender

    @BeforeAll
    fun init() {
        val logger = LoggerFactory.getLogger(CrmSenderProvider::class.java) as Logger
        memoryAppender = MemoryAppender()
        memoryAppender.context = LoggerFactory.getILoggerFactory() as LoggerContext
        logger.level = Level.DEBUG
        logger.addAppender(memoryAppender)
        memoryAppender.start()
    }

    @Test
    fun `send event to crm - event sent success`() {
        val MSG = "Send to crm"
        val crm = CrmSenderProvider()
        crm.send(CustomerOrderId(1L), price(BigDecimal("10")))

        val searchLog = memoryAppender.search(MSG, Level.INFO)
        searchLog.size shouldBe 1
        searchLog[0].message shouldBe "Send to crm order with id = 1 money 10.00"
        memoryAppender.reset()
    }
}