package com.adurandet.weather.model

data class SearchRequest(
    val lat: Double? = null,
    val long: Double? = null,
    val id: String? = null,
    val cityName: String? = null,
    val zipCode: String? = null
)