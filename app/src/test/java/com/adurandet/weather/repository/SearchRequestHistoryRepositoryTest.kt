package com.adurandet.weather.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adurandet.weather.database.SearchRequestDao
import com.adurandet.weather.mock
import com.adurandet.weather.mockId
import com.adurandet.weather.mockName
import com.adurandet.weather.model.SearchRequest
import junit.framework.Assert
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SearchRequestHistoryRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val searchRequestDao: SearchRequestDao = mock()

    private lateinit var searchRequestHistoryRepository: SearchRequestHistoryRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        searchRequestHistoryRepository = SearchRequestHistoryRepository(searchRequestDao)
    }


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun insertRequestTest() {
        runBlocking {

            val searchRequest = SearchRequest(id = mockId, cityName = mockName)
            Mockito.doNothing().`when`(searchRequestDao).insert(searchRequest)

            searchRequestHistoryRepository.insert(searchRequest)

            Mockito.verify(searchRequestDao).insert(searchRequest)

        }
    }

    @Test
    fun getSearchRequestHistoryTest() {
        runBlocking {

            val searchRequestHistory = listOf(SearchRequest(id = mockId, cityName = mockName))
            Mockito.doReturn(searchRequestHistory).`when`(searchRequestDao).getAll()

            val result = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

            Mockito.verify(searchRequestDao).getAll()
            Assert.assertEquals(searchRequestHistory, result)

        }
    }
}