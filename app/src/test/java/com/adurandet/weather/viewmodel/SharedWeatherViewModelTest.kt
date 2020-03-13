package com.adurandet.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adurandet.weather.forClass
import com.adurandet.weather.mock
import com.adurandet.weather.mockName
import com.adurandet.weather.ui.main.viewmodel.SharedWeatherViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SharedWeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val observer: Observer<String> = mock()

    private lateinit var sharedWeatherViewModel: SharedWeatherViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        sharedWeatherViewModel = SharedWeatherViewModel()
        sharedWeatherViewModel.loadSearchRequestLiveData.observeForever(observer)
    }

    @Test
    fun searchWeatherByCityNameTest() {

        sharedWeatherViewModel.setSearchRequestToLoad(mockName)

        val captor: ArgumentCaptor<String> = forClass()
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            Assert.assertEquals(mockName, value)
        }

    }

}