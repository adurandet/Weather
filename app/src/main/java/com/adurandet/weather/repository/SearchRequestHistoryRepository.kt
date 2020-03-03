package com.adurandet.weather.repository

import com.adurandet.weather.database.SearchRequestDao
import com.adurandet.weather.model.SearchRequest
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchRequestHistoryRepository: KoinComponent {

    private val searchRequestDao: SearchRequestDao by inject()

    suspend fun getSearchRequestHistoryAsync(): Deferred<List<SearchRequest>> = withContext(Dispatchers.IO) { async { searchRequestDao.getAll() } }

    suspend fun insert(searchRequest: SearchRequest): Job = withContext(Dispatchers.IO) {
        launch {
            searchRequest.createdAt = System.currentTimeMillis()
            searchRequestDao.insert(searchRequest)
        }
    }
}
