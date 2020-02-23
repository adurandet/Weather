package com.adurandet.weather.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adurandet.weather.database.SearchRequestDao
import com.adurandet.weather.model.*
import com.adurandet.weather.network.*
import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.utils.toIconUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class WeatherRepository(private val apiHelper: ApiHelper, private val searchRequestDao: SearchRequestDao): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

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

            launch {
                searchRequestDao.insert(SearchRequest(id = it.id, cityName = it.name))
            }
            weatherLiveData.value = Success(weather)

        } ?: run {
            triggerGetWeatherError(DataNotFoundError())
        }
    }

    private fun triggerGetWeatherError(codeError: CodeError) {
        weatherLiveData.value =
            Failure(codeError)
    }

    fun getWeatherSearchHistory() = searchRequestDao.getAll()
}

fun GetWeatherResponse.toWeather() =
    Weather(
        id,
        name,
        weather[0].description,
        main.temp,
        weather[0].icon.toIconUrl()
    )
