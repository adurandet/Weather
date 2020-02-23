package com.adurandet.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.adurandet.weather.*
import com.adurandet.weather.model.DataNotFoundError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.*
import com.adurandet.weather.ui.main.viewmodel.MainWeatherViewModel
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainWeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val observer: Observer<Resource<Weather?>> = mock()
    private val weatherRepository: WeatherRepository = mock()

    private lateinit var mainWeatherViewModel: MainWeatherViewModel

    @Before
    fun setupViewModel() {
        MockitoAnnotations.initMocks(this)
        mainWeatherViewModel = MainWeatherViewModel(weatherRepository)
        mainWeatherViewModel.weatherLiveData.observeForever(observer)
    }

    @Test
    fun searchWeatherLoadingTest(){
        val mockWeatherLiveDataLoading = MutableLiveData(Loading<Weather?>())
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveDataLoading).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.search(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Loading<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveDataLoading.value, value)
        }
    }

    @Test
    fun searchWeatherFailure(){
        val codeError = DataNotFoundError()
        val mockWeatherLiveData = MutableLiveData(Failure<Weather?>(codeError))
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.search(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Failure<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun searchWeatherByCityNameTest(){

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.search(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun searchWeatherByZipCodeTest(){

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(zipCode = mockZipCode)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.search(mockZipCode)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun noSearchWeatherEmptyStringTest(){
        mainWeatherViewModel.search("")
        Mockito.verifyZeroInteractions(weatherRepository)
    }

    @Test
    fun searchWeatherByLatLongTest(){

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(lat = mockLat, long = mockLong)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.search(LatLng(mockLat, mockLong))

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

}