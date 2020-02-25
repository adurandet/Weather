package com.adurandet.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adurandet.weather.forClass
import com.adurandet.weather.mock
import com.adurandet.weather.mockId
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.Resource
import com.adurandet.weather.repository.SearchRequestHistoryRepository
import com.adurandet.weather.repository.Success
import com.adurandet.weather.ui.main.viewmodel.SearchRequestHistoryViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.atLeast
import org.mockito.MockitoAnnotations

class SearchRequestHistoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val observer: Observer<Resource<List<SearchRequest>?>> = mock()
    private val searchRequestHistoryRepository: SearchRequestHistoryRepository = mock()

    private lateinit var searchRequestViewModel: SearchRequestHistoryViewModel

    @Before
    fun setupUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getSearchRequestHistoryTest() {
        runBlocking {

            val mockSearchRequestHistoryList = listOf(SearchRequest(id = mockId))
            val mockSearchRequestRepoResponse = GlobalScope.async { mockSearchRequestHistoryList }
            Mockito.doReturn(mockSearchRequestRepoResponse).`when`(searchRequestHistoryRepository)
                .getSearchRequestHistoryAsync()

            runBlocking {
                searchRequestViewModel = SearchRequestHistoryViewModel(searchRequestHistoryRepository)
                searchRequestViewModel.searchRequestHistory.observeForever(observer)
            }

            Mockito.verify(searchRequestHistoryRepository).getSearchRequestHistoryAsync()

            val loadingCaptor: ArgumentCaptor<Resource<List<SearchRequest>?>> = forClass()
            loadingCaptor.run {
                Mockito.verify(observer, atLeast(1)).onChanged(capture())
                Assert.assertEquals(mockSearchRequestHistoryList, (value as? Success)?.data)
            }
        }
    }

}