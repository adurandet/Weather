package com.adurandet.weather.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adurandet.weather.model.Search
import com.adurandet.weather.model.Weather
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.network.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(private val apiHelper: ApiHelper) {

    private val weatherLiveData = MutableLiveData<Resource<Weather>>()

    fun getWeather(
        search: Search
    ): LiveData<Resource<Weather>> {

        weatherLiveData.value = Resource.loading(weatherLiveData.value?.data)

        val apiCall = with(search) { when {
            lat != null && long != null -> apiHelper.getWeatherByLatLong(lat, long)

            !id.isNullOrEmpty() -> apiHelper.getWeatherById(id)

            !cityName.isNullOrEmpty() -> apiHelper.getWeatherByCityName(cityName)

            else -> null
        }}


        apiCall?.run {

            enqueue(object : Callback<GetWeatherResponse> {

                override fun onFailure(call: Call<GetWeatherResponse>, t: Throwable) {
                    Log.e("WeatherRepository", t.message)
                    weatherLiveData.value = Resource.error(t.message ?: "", null)
                }

                override fun onResponse(
                    call: Call<GetWeatherResponse>, response: Response<GetWeatherResponse>
                ) {
                    Log.d(
                        "WeatherRepository",
                        "getCityWeather successful: ${response.isSuccessful}"
                    )

                    val weather = response.body()?.toWeather()
                    weatherLiveData.value = Resource.success(weather)
                }
            })

        } ?: run {
            weatherLiveData.value = Resource.error("", null)
        }

        return weatherLiveData
    }
}

private fun GetWeatherResponse.toWeather() =
    Weather(id, name, weather.description, main.temp, weather.icon)

