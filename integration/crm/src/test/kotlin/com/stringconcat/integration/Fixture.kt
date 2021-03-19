package com.stringconcat.integration

import arrow.core.Either
import com.stringconcat.ddd.order.domain.menu.Price
import java.math.BigDecimal
import kotlin.random.Random

fun price(value: BigDecimal = BigDecimal(Random.nextInt(1, 500000))): Price {
    val result = Price.from(value)
    check(result is Either.Right<Price>)
    return result.b
}