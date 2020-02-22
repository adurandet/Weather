package com.adurandet.weather.utils

import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun dataUtilsRoundNumberNotNumberTest() {
        val testData = "Not a number"
        val result = testData.roundNumber()

        assert(result == NOT_A_NUMBER_RESULT)
    }

    @Test
    fun dataUtilsRoundNumberTest() {
        val testData = "123.456789"
        val result = testData.roundNumber()

        Assert.assertEquals("123.5", result)
    }

    @Test
    fun iconToIconUrlTest(){
        val testData = "10d"
        val result = testData.toIconUrl()

        Assert.assertEquals("https://openweathermap.org/img/wn/$testData@2x.png", result)
    }

}