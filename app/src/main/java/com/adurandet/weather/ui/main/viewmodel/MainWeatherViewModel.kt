package com.adurandet.weather.ui.main.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.network.Resource

class MainWeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _triggerSearchLiveData = MutableLiveData<SearchRequest>()

    val weatherLiveData: LiveData<Resource<Weather?>> =
        Transformations.switchMap(_triggerSearchLiveData) {
            weatherRepository.getWeather(it)
        }

    fun search(search: String) {

        val searchRequest = if (TextUtils.isDigitsOnly(search)) {
            SearchRequest(zipCode = search)
        } else {
            SearchRequest(cityName = search)
        }

        _triggerSearchLiveData.value = searchRequest
    }

}
