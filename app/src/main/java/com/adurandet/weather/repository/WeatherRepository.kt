package com.adurandet.weather.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adurandet.weather.model.*
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.utils.toIconUrl
import org.koin.core.KoinComponent
import org.koin.java.KoinJavaComponent.get
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository( private val apiHelper: ApiHelper = get(ApiHelper::class.java)): KoinComponent {

    private val weatherLiveData = MutableLiveData<Resource<Weather?>>()

    fun getWeather(
        searchRequest: SearchRequest
    ): LiveData<Resource<Weather?>> {

        weatherLiveData.value =
            Loading((weatherLiveData.value as? Success)?.data)

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
            // apiCall ready, trigger the call
            enqueue(object : Callback<GetWeatherResponse> {

                override fun onFailure(call: Call<GetWeatherResponse>, t: Throwable) {
                    Log.e("WeatherRepository", t.message ?: "")
                    triggerGetWeatherError(CallError(t.message ?: ""))
                }

                override fun onResponse( call: Call<GetWeatherResponse>, response: Response<GetWeatherResponse>) {
                    Log.d(
                        "WeatherRepository",
                        "getCityWeather successful: ${response.isSuccessful}"
                    )

                    when (response.code()) {

                        200 -> processValidResult(response)

                        400 -> triggerGetWeatherError(BadRequestError())

                        404 -> triggerGetWeatherError(DataNotFoundError())

                        // More HTTP Code should be managed.
                        // A class implementing CallBack<T> should be created to manage
                        // the response for every request
                        else -> triggerGetWeatherError(CallError(response.message()))

                    }


                }
            })

        } ?: run {
            // No apiCall, return an error
            triggerGetWeatherError(BadRequestError())
        }

        return weatherLiveData
    }

    private fun processValidResult(response: Response<GetWeatherResponse>) {

        val weather = response.body()?.toWeather()
        weather?.let {
            weatherLiveData.value = Success(weather)
        } ?: run {
            triggerGetWeatherError(DataNotFoundError())
        }

    }

    private fun triggerGetWeatherError(codeError: CodeError) {
        weatherLiveData.value =
            Failure(codeError)
    }

}

fun GetWeatherResponse.toWeather() =
    Weather(
        id,
        name,
        weather[0].description,
        main.temp,
        weather[0].icon.toIconUrl()
    )
