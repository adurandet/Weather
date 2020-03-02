package com.adurandet.weather.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adurandet.weather.model.DataBaseError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchRequestHistoryViewModel(private val searchRequestHistoryRepository: SearchRequestHistoryRepository) :
    ViewModel() {

    val searchRequestHistory: MutableLiveData<Resource<List<SearchRequest>>> = MutableLiveData()

    init {
        viewModelScope.launch {

            searchRequestHistory.value = Loading()

            loadSearchRequestHistory()

        }
    }

    fun onDeleteItemClicked(id: String) {

        Log.d("SearchRequestHistoryVM", "onDeleteItemClicked id: $id")
        searchRequestHistory.value = Loading((searchRequestHistory.value as? Success)?.data)

        viewModelScope.launch {
            Log.d("SearchRequestHistoryVM", "deleteSearchRequestHistoryAsync call")
            deleteSearchRequestHistoryAsync(id).await()

            Log.d("SearchRequestHistoryVM", "loadSearchRequestHistory call")
            loadSearchRequestHistory()

        }
        Log.d("SearchRequestHistoryVM", "onDeleteItemClicked end of Method")

    }

    private suspend fun deleteSearchRequestHistoryAsync(id: String) =
        viewModelScope.async { searchRequestHistoryRepository.delete(id) }

    private suspend fun loadSearchRequestHistory() {
        try {

            val searchRequest = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

            searchRequestHistory.value = Success(searchRequest)

        } catch (e: Exception) {

            searchRequestHistory.value = Failure(DataBaseError())

        }
    }

}
