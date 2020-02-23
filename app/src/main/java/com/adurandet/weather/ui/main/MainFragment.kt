package com.adurandet.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adurandet.weather.R
import com.adurandet.weather.component.DebounceTextWatcher
import com.adurandet.weather.model.*
import com.adurandet.weather.network.*
import com.adurandet.weather.repository.WeatherRepository
import com.adurandet.weather.ui.main.viewmodel.MainWeatherViewModel
import com.adurandet.weather.ui.main.viewmodel.WeatherViewModelProviderFactory
import com.adurandet.weather.utils.showError
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*

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

        val view = inflater.inflate(R.layout.main_fragment, container, false)
        initListeners(view)
        return view
    }

    private fun initListeners(view: View) {

        view.weather_fragment_search_edt.addTextChangedListener(object : DebounceTextWatcher() {
            override fun afterDebounceTextChanged(s: String) {
                mainWeatherViewModel.search(s)
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainWeatherViewModel.weatherLiveData.observe(this, Observer {
            processWeatherSearchResult(it)
        })

    }

    private fun processWeatherSearchResult(weatherResource: Resource<Weather?>) {

        with(weatherResource) {
            processWeatherSearchLoading(this is Loading)
            when (this) {
                is Success -> processWeatherSearchSuccess(data)

                is Failure -> processWeatherSearchError(codeError)
            }
        }

    }

    private fun processWeatherSearchLoading(showWeatherLoader: Boolean) {

        if (showWeatherLoader)
            weather_fragment_progress_bar.show()
        else
            weather_fragment_progress_bar.hide()

    }

    private fun processWeatherSearchError(codeError: CodeError) {
        val message = when (codeError){
            is CallError -> codeError.message
            is BadRequestError -> getString(R.string.wrong_search_request)
            is DataNotFoundError -> getString(R.string.weather_not_found)
        }
        showError(message)
    }

    private fun processWeatherSearchSuccess(data: Weather?) {
        data?.let {
            weather_fragment_weather_card.setWeather(data)
            weather_fragment_weather_card.isVisible = true
        }
    }

}
