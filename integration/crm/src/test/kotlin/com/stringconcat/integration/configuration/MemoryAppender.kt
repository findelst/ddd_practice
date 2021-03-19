package com.stringconcat.integration.configuration

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender

class MemoryAppender : ListAppender<ILoggingEvent?>() {
    fun reset() {
        this.list.clear()
    }

    fun contains(string: String, level: Level): Boolean {
        return this.list.any {
            it?.message?.contains(string) ?: false && it?.level == level
        }
    }

    fun search(string: String, level: Level): List<ILoggingEvent> {
        return this.list
            .filter { event ->
                event?.message?.contains(string) ?: false && event?.level == level
            }
            .filterNotNull()
    }
}