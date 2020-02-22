package com.adurandet.weather.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun String.toIconUrl() = "https://openweathermap.org/img/wn/$this@2x.png"

fun String.roundNumber(numberOfDecimal: Int = 1): String {
    return try {
        BigDecimal(this).setScale(numberOfDecimal, RoundingMode.HALF_EVEN).toString()
    } catch (e : NumberFormatException){
        "X.X"
    }
}