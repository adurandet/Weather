package com.adurandet.weather.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adurandet.weather.*
import com.adurandet.weather.model.*
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.network.response.GetWeatherResponse
import com.adurandet.weather.mock
import com.adurandet.weather.utils.toIconUrl
import junit.framework.Assert
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls

class WeatherRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val callMocked: Call<GetWeatherResponse> =
        mock()
    private val apiHelperMocked: ApiHelper = mock()
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setupViewModel() {
        MockitoAnnotations.initMocks(this)
        weatherRepository = WeatherRepository(apiHelperMocked)
    }

    @Test
    fun getWeatherWithIdTest(){

        val call = Calls.response(mockGetWeatherResponse)
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherById(mockId)

        val searchRequest = SearchRequest(id = mockId)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        val successData = Success(mockWeatherModel)

        Mockito.verify(apiHelperMocked).getWeatherById(mockId)
        Assert.assertEquals( successData, weatherLiveData.value)

    }

    @Test
    fun getWeatherWithLatLongTest(){

        val call = Calls.response(mockGetWeatherResponse)
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherByLatLong(mockLat, mockLong)

        val searchRequest = SearchRequest(lat = mockLat, long = mockLong)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        val successData = Success(mockWeatherModel)

        Mockito.verify(apiHelperMocked).getWeatherByLatLong(mockLat, mockLong)
        Assert.assertEquals( successData, weatherLiveData.value)

    }

    @Test
    fun getWeatherWithZipCodeTest(){

        val call = Calls.response(mockGetWeatherResponse)
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherByZipCode(mockZipCode)

        val searchRequest = SearchRequest(zipCode = mockZipCode)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        val successData = Success(mockWeatherModel)

        Mockito.verify(apiHelperMocked).getWeatherByZipCode(mockZipCode)
        Assert.assertEquals( successData, weatherLiveData.value)

    }

    @Test
    fun getWeatherWithCityNameTest(){

        val call = Calls.response(mockGetWeatherResponse)
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherByCityName(mockName)

        val searchRequest = SearchRequest(cityName = mockName)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        val weather = Weather(
            mockId,
            mockName,
            mockWeather.description,
            mockMain.temp,
            mockWeather.icon.toIconUrl()
        )
        val successData = Success(weather)

        Mockito.verify(apiHelperMocked).getWeatherByCityName(mockName)
        Assert.assertEquals( successData, weatherLiveData.value)

    }

    @Test
    fun getWeatherLoading(){

        Mockito.doReturn(callMocked).`when`(apiHelperMocked).getWeatherById(mockId)
        Mockito.doNothing().`when`(callMocked).enqueue(ArgumentMatchers.any())

        val searchRequest = SearchRequest(id = mockId)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)


        Mockito.verify(apiHelperMocked).getWeatherById(mockId)
        Assert.assertEquals(Loading<GetWeatherResponse>(), weatherLiveData.value)

    }

    @Test
    fun getWeatherServerFailureTest(){

        val call = Calls.failure<GetWeatherResponse>(Throwable(mockCallErrorMessage, null))
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherById(mockId)

        val searchRequest = SearchRequest(id = mockId)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        Mockito.verify(apiHelperMocked).getWeatherById(mockId)
        assert( weatherLiveData.value is Failure)
        assert( (weatherLiveData.value as Failure).codeError is CallError)
        Assert.assertEquals( mockCallErrorMessage , (weatherLiveData.value as Failure).codeError.message)

    }

    @Test
    fun getWeatherWrongSearchRequestFailureTest(){

        val searchRequest = SearchRequest()
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        Mockito.verifyZeroInteractions(apiHelperMocked)
        assert( weatherLiveData.value is Failure)
        assert( (weatherLiveData.value as Failure).codeError is BadRequestError)

    }

    @Test
    fun getWeatherDataNotFoundTest(){

        val responseBody = ResponseBody.create(null, "")
        val response = Response.error<GetWeatherResponse>(404, responseBody)

        val call = Calls.response(response)
        Mockito.doReturn(call).`when`(apiHelperMocked).getWeatherById(mockId)

        val searchRequest = SearchRequest(id = mockId)
        val weatherLiveData = weatherRepository.getWeather(searchRequest)

        Mockito.verify(apiHelperMocked).getWeatherById(mockId)
        assert( weatherLiveData.value is Failure)
        assert( (weatherLiveData.value as Failure).codeError is DataNotFoundError)

    }

}