package com.adurandet.weather.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.Resource
import com.adurandet.weather.repository.Success
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.utils.isNumberOnly
import com.google.android.gms.maps.model.LatLng

class MainWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val LAST_WEATHER_ID = "LAST_WEATHER_ID"
    }

    private lateinit var lastSearchId: String

    private val _triggerSearchLiveData = MutableLiveData<SearchRequest>()

    private val _weatherLiveData = Transformations.switchMap(_triggerSearchLiveData) {
        weatherRepository.getWeather(it)
    }

    val weatherLiveData: LiveData<Resource<Weather?>> = Transformations.map(_weatherLiveData) { weatherResponse ->

        if (weatherResponse is Success) {
            weatherResponse.data?.let {
                lastSearchId = it.id
                savedStateHandle.set(LAST_WEATHER_ID, it.id)
            }
        }

        weatherResponse
    }

    private val _triggerGetSearchRequestHistory = MutableLiveData<Boolean>()

    private val _searchRequestHistory = Transformations.switchMap(_triggerGetSearchRequestHistory) {
        weatherRepository.getWeatherSearchHistory()
    }

    val searchRequestHistory = Transformations.map(_searchRequestHistory) { searchRequestHistory ->

        if (lastSearchId.isEmpty() && searchRequestHistory.isNotEmpty()) {
            searchById(searchRequestHistory[0].id)
        }

        searchRequestHistory
    }

    init {

        lastSearchId = savedStateHandle[LAST_WEATHER_ID] ?: ""

        if (lastSearchId.isNotEmpty()) {
            searchById(lastSearchId)
        } else {
            getSearchRequestHistory()
        }

    }


    fun search(search: String) {

        if (search.isEmpty()) return

        val searchRequest = if (search.isNumberOnly()) {
            SearchRequest(zipCode = search)
        } else {
            SearchRequest(cityName = search)
        }

        _triggerSearchLiveData.value = searchRequest

    }

    fun search(latLong: LatLng) {

        val searchRequest = SearchRequest(lat = latLong.latitude, long = latLong.longitude)

        _triggerSearchLiveData.value = searchRequest

    }

    private fun searchById(weatherId: String) {

        val searchRequest = SearchRequest(id = weatherId)

        _triggerSearchLiveData.value = searchRequest

    }

    private fun getSearchRequestHistory() {
        _triggerGetSearchRequestHistory.value = true
    }

}
