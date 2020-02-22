package com.adurandet.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adurandet.weather.R
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.ui.main.viewmodel.MainWeatherViewModel
import com.adurandet.weather.ui.main.viewmodel.WeatherViewModelProviderFactory

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val apiHelper = ApiHelper()
    private val weatherRepository = WeatherRepository(apiHelper)
    private val weatherViewModelFactory = WeatherViewModelProviderFactory(weatherRepository)
    private val mainWeatherViewModel: MainWeatherViewModel by viewModels {
        weatherViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainWeatherViewModel.weatherLiveData

    }

}
