package com.adurandet.weather.repository

import com.adurandet.weather.database.SearchRequestDao
import com.adurandet.weather.model.SearchRequest
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.java.KoinJavaComponent.get

class SearchRequestHistoryRepository(private val searchRequestDao: SearchRequestDao = get(SearchRequestDao::class.java)): KoinComponent {

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
