package com.adurandet.weather.utils

import com.adurandet.weather.BuildConfig
import java.math.BigDecimal
import java.math.RoundingMode

const val NOT_A_NUMBER_RESULT = "X.X"

fun String.toIconUrl() = String.format(BuildConfig.BASE_IMAGE_URL, this)

fun String.roundNumber(numberOfDecimal: Int = 1): String {
    return try {
        BigDecimal(this).setScale(numberOfDecimal, RoundingMode.HALF_EVEN).toString()
    } catch (e : NumberFormatException){
        NOT_A_NUMBER_RESULT
    }
}

fun String.isNumberOnly() = matches(Regex("^[0-9]*$"))