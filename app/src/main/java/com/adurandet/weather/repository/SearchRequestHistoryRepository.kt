package com.adurandet.weather.repository

import com.adurandet.weather.database.SearchRequestDao
import com.adurandet.weather.model.SearchRequest
import kotlinx.coroutines.*

class SearchRequestHistoryRepository(private val searchRequestDao: SearchRequestDao) {

    suspend fun getSearchRequestHistoryAsync(): Deferred<List<SearchRequest>> =
        withContext(Dispatchers.IO) { async { searchRequestDao.getAll() } }

    suspend fun insert(searchRequest: SearchRequest): Job = withContext(Dispatchers.IO) {
        launch {
            searchRequest.createdAt = System.currentTimeMillis()
            searchRequestDao.insert(searchRequest)
        }
    }

    suspend fun delete(id: String): Job = withContext(Dispatchers.IO) {
        launch {
            searchRequestDao.delete(id)
        }
    }
}
