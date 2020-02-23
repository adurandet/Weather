package com.adurandet.weather

import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.network.response.Main
import com.adurandet.weather.network.response.Weather
import com.adurandet.weather.utils.toIconUrl

const val mockCallErrorMessage = "call error message"

const val mockId = "123"

const val mockName = "San Francisco"

const val mockLat = 37.7749
const val mockLong = 122.4194

const val mockZipCode = "94124"

val mockWeather = Weather(
    "Light rain",
    "09d"
)

val mockMain = Main("13.75")

val mockGetWeatherResponse = GetWeatherResponse(
    mockId,
    mockName,
    listOf(mockWeather),
    mockMain
)

val mockWeatherModel = com.adurandet.weather.model.Weather(
    mockId,
    mockName,
    mockWeather.description,
    mockMain.temp,
    mockWeather.icon.toIconUrl()
)
