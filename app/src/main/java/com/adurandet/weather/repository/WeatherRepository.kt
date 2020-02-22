package com.adurandet.weather.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.network.Resource
import com.adurandet.weather.utils.toIconUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(private val apiHelper: ApiHelper) {

    private val weatherLiveData = MutableLiveData<Resource<Weather>>()

    fun getWeather(
        searchRequest: SearchRequest
    ): LiveData<Resource<Weather>> {

        weatherLiveData.value = Resource.loading(weatherLiveData.value?.data)

        val apiCall = with(searchRequest) {
            when {
                !id.isNullOrEmpty() -> apiHelper.getWeatherById(id)

                lat != null && long != null -> apiHelper.getWeatherByLatLong(lat, long)

                !zipCode.isNullOrEmpty() -> apiHelper.getWeatherByZipCode(zipCode)

                !cityName.isNullOrEmpty() -> apiHelper.getWeatherByCityName(cityName)

                else -> null
            }
        }

        apiCall?.run {

            enqueue(object : Callback<GetWeatherResponse> {

                override fun onFailure(call: Call<GetWeatherResponse>, t: Throwable) {
                    Log.e("WeatherRepository", t.message)
                    triggerGetWeatherError(t.message)
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
            weatherLiveData.value = Resource.error("Wrong Search request", null)
        }

        return weatherLiveData
    }

    private fun triggerGetWeatherError(message: String?) {
        weatherLiveData.value = Resource.error(message ?: "", null)
    }
}

private fun GetWeatherResponse.toWeather() =
    Weather(
        id,
        name,
        weather[0].description,
        main.temp,
        weather[0].icon.toIconUrl()
    )

