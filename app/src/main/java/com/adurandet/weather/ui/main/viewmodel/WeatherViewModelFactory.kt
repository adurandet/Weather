package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adurandet.weather.repository.WeatherRepository

class WeatherViewModelProviderFactory(private val weatherRepository: WeatherRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainWeatherViewModel::class.java) ->
                MainWeatherViewModel(weatherRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}