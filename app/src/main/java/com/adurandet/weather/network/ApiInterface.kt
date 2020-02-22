package com.adurandet.weather.network

import com.adurandet.weather.network.response.GetWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    fun getWeatherByLatLong(
        @Query("lat") lat: Double,
        @Query("long") long: Double
    ): Call<GetWeatherResponse>

    @GET("weather")
    fun getWeatherById(@Query("id") id: String): Call<GetWeatherResponse>

    @GET("weather")
    fun getWeatherByCityName(@Query("q") cityName: String): Call<GetWeatherResponse>

    @GET("weather")
    fun getWeatherByZipCode(@Query("zip") cityName: String): Call<GetWeatherResponse>

}