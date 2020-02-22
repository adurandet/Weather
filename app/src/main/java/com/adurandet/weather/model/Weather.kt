package com.adurandet.weather.model

data class Weather(
    val id: String,
    val name: String,
    val description: String,
    val temperature: String,
    val iconURl: String
)