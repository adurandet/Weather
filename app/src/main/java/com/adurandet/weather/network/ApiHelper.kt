package com.adurandet.weather.network

import com.adurandet.weather.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiHelper private constructor() {

    companion object {
        const val UNITS = "metric"

        @Volatile
        private var instance: ApiHelper? = null

        fun getIntance() = instance ?: synchronized(this) {

            ApiHelper().also { instance = it }

        }

    }

    private var apiInterface: ApiInterface

    init {
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request()

            val url = request.url().newBuilder()
                .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
                // Units could be customisable by the user, and passed as a function parameter.
                .addQueryParameter("units", UNITS)
                .build()

            val requestBuilder = request.newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            requestBuilder.url(url)

            chain.proceed(requestBuilder.build())

        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()

        val gson = GsonBuilder().create()

        val retrofit: Retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiInterface = retrofit.create(ApiInterface::class.java)
    }

    fun getWeatherByLatLong(lat: Double, long: Double) = apiInterface.getWeatherByLatLong(lat, long)

    fun getWeatherById(id: String) = apiInterface.getWeatherById(id)

    fun getWeatherByCityName(cityName: String) = apiInterface.getWeatherByCityName(cityName)

    fun getWeatherByZipCode(zipCode: String) = apiInterface.getWeatherByZipCode(zipCode)

}