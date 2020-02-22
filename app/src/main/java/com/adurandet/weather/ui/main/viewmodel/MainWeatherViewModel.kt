package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adurandet.weather.model.Search
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.network.Resource

class MainWeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _triggerSearchLiveData = MutableLiveData<Search>()

    val weatherLiveData: LiveData<Resource<Weather>> = Transformations.switchMap(_triggerSearchLiveData) {
        weatherRepository.getWeather(it)
    }

    fun search(
        lat: Double? = null,
        long: Double? = null,
        id: String? = null,
        cityName: String? = null
    ) {
        _triggerSearchLiveData.value = Search(lat, long, id, cityName)
    }

}
