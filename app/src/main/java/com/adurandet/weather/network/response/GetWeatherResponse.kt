package com.adurandet.weather.network.response

class GetWeatherResponse(
    val id: String,
    val name: String,
    val weather: Weather,
    val main: Main
)