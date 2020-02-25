package com.adurandet.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.adurandet.weather.*
import com.adurandet.weather.model.DataNotFoundError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.model.Weather
import com.adurandet.weather.repository.*
import com.adurandet.weather.ui.main.viewmodel.MainWeatherViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainWeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val observer: Observer<Resource<Weather?>> = mock()
    private val searchRequestHistoryRepository: SearchRequestHistoryRepository = mock()
    private val weatherRepository: WeatherRepository = mock()

    private lateinit var mainWeatherViewModel: MainWeatherViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(TestCoroutineDispatcher())
        runBlocking {
            val deferred = CompletableDeferred<Resource<List<SearchRequest>?>>()
            Mockito.doReturn(deferred).`when`(searchRequestHistoryRepository).getSearchRequestHistoryAsync()

            mainWeatherViewModel = MainWeatherViewModel(weatherRepository, searchRequestHistoryRepository, SavedStateHandle())
            mainWeatherViewModel.weatherLiveData.observeForever(observer)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchWeatherLoadingTest() {
        val mockWeatherLiveDataLoading = MutableLiveData(Loading<Weather?>())
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveDataLoading).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.searchByCityNameOrZipCode(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Loading<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveDataLoading.value, value)
        }
    }

    @Test
    fun searchWeatherFailure() {
        val codeError = DataNotFoundError()
        val mockWeatherLiveData = MutableLiveData(Failure<Weather?>(codeError))
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.searchByCityNameOrZipCode(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Failure<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun searchWeatherByCityNameTest() {

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(cityName = mockName)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.searchByCityNameOrZipCode(mockName)

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        runBlocking {
            Mockito.verify(searchRequestHistoryRepository).insert(searchRequest.apply { id = mockId })
        }

        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun searchWeatherByZipCodeTest() {

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(zipCode = mockZipCode)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.searchByCityNameOrZipCode(mockZipCode)
        runBlocking {
            Mockito.verify(searchRequestHistoryRepository).insert(SearchRequest( id = mockId, cityName = mockName ))
        }

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

    @Test
    fun noSearchWeatherEmptyStringTest() {
        mainWeatherViewModel.searchByCityNameOrZipCode("")
        Mockito.verifyZeroInteractions(weatherRepository)
    }

    @Test
    fun searchWeatherByLatLongTest() {

        val mockWeatherLiveData = MutableLiveData(Success<Weather?>(mockWeatherModel))
        val searchRequest = SearchRequest(lat = mockLat, long = mockLong)
        Mockito.doReturn(mockWeatherLiveData).`when`(weatherRepository).getWeather(
            searchRequest
        )

        mainWeatherViewModel.searchByCityNameOrZipCode(LatLng(mockLat, mockLong))
        runBlocking {
            Mockito.verify(searchRequestHistoryRepository).insert(SearchRequest(id = mockId, cityName = mockName))
        }

        Mockito.verify(weatherRepository).getWeather(searchRequest)
        val captor: ArgumentCaptor<Success<Weather>> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockWeatherLiveData.value, value)
        }
    }

}