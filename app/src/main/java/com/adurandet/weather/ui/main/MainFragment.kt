package com.adurandet.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.adurandet.weather.R
import com.adurandet.weather.component.DebounceTextWatcher
import com.adurandet.weather.database.AppDataBase
import com.adurandet.weather.interactor.LocationInteractor
import com.adurandet.weather.model.*
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.repository.*
import com.adurandet.weather.ui.main.viewmodel.MainWeatherViewModel
import com.adurandet.weather.ui.main.viewmodel.WeatherViewModelProviderFactory
import com.adurandet.weather.utils.showError
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment(), LocationInteractor.Callback {

    private val apiHelper  by lazy { ApiHelper.getIntance() }

    private val searchRequestDao by lazy {
        AppDataBase.getInstance(requireContext()).searchRequestDao()
    }

    private val weatherRepository by lazy {
        WeatherRepository(apiHelper)
    }

    private val searchRequestHistoryRepository by lazy {
        SearchRequestHistoryRepository(searchRequestDao)
    }

    private val mainWeatherViewModel: MainWeatherViewModel by viewModels {
        WeatherViewModelProviderFactory(weatherRepository, searchRequestHistoryRepository, this)
    }

    private lateinit var locationInteractor: LocationInteractor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

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

        view.weather_fragment_use_my_location_button.setOnClickListener {
            view.weather_fragment_search_edt.text.clear()
            locationInteractor.verifyLocationPermissionsAndGetLocation()
        }

        view.weather_fragment_show_history_button.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_weather_search_history)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { locationInteractor = LocationInteractor(it, this) }

        mainWeatherViewModel.weatherLiveData.observe(this, Observer {
            processWeatherSearchResult(it)
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_REQUEST_CODE -> locationInteractor.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    override fun onLocationReceived(latLng: LatLng) {
        mainWeatherViewModel.search(latLng)
    }

    override fun onLocationPermissionsNotGranted() {
        showError(getString(R.string.location_not_granted))
    }

    override fun onLocationNotReceived() {
        showError(getString(R.string.no_location_received))
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
        val message = when (codeError) {
            is CallError -> codeError.message
            is BadRequestError -> getString(R.string.wrong_search_request)
            is DataNotFoundError -> getString(R.string.weather_not_found)
            is DataBaseError -> getString(R.string.data_base_error)
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
