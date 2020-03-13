package com.adurandet.weather.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.Resource
import com.adurandet.weather.repository.SearchRequestHistoryRepository
import com.adurandet.weather.repository.Success
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.utils.isNumberOnly
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.java.KoinJavaComponent.get

class MainWeatherViewModel(
    private val weatherRepository: WeatherRepository = get(WeatherRepository::class.java),
    private val searchRequestHistoryRepository: SearchRequestHistoryRepository = get(SearchRequestHistoryRepository::class.java),
    private val state: SavedStateHandle
) : ViewModel(), KoinComponent {

    companion object {
        const val LAST_WEATHER_CITY_NAME = "LAST_WEATHER_ID"
    }

    private val _triggerSearchLiveData = MutableLiveData<SearchRequest>()

    private val _weatherLiveData = Transformations.switchMap(_triggerSearchLiveData) {
        weatherRepository.getWeather(it)
    }

    val weatherLiveData: LiveData<Resource<Weather?>> = Transformations.map(_weatherLiveData) { weatherResponse ->

        if (weatherResponse is Success) {
            weatherResponse.data?.let {

                viewModelScope.launch {
                    searchRequestHistoryRepository.insert(SearchRequest(cityName = it.name))
                }

                state.set(LAST_WEATHER_CITY_NAME, it.name)
            }
        }

        weatherResponse
    }

    init {

        val lastSearchCityName = state[LAST_WEATHER_CITY_NAME] ?: ""
        Log.d("lastSearchCityName", lastSearchCityName)

        if (lastSearchCityName.isNotEmpty()) {
            searchByCityNameOrZipCode(lastSearchCityName)
        } else {
            searchWithLastSearchRequestHistory()
        }

    }

    private fun searchWithLastSearchRequestHistory() {
        viewModelScope.launch {

            val searchHistoryList = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

            if (searchHistoryList.isNotEmpty()) {
                searchByCityNameOrZipCode(searchHistoryList[0].cityName)
            }

        }
    }

    fun searchByCityNameOrZipCode(search: String) {

        if (search.isEmpty()) return

        val searchRequest = if (search.isNumberOnly()) {
            SearchRequest(zipCode = search)
        } else {
            SearchRequest(cityName = search)
        }

        _triggerSearchLiveData.value = searchRequest

    }

    fun searchByLatLong(latLong: LatLng) {

        val searchRequest = SearchRequest(lat = latLong.latitude, long = latLong.longitude)

        _triggerSearchLiveData.value = searchRequest

    }

}
