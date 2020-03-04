package com.adurandet.weather.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import androidx.lifecycle.*
import com.adurandet.weather.model.DataBaseError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchRequestHistoryViewModel(private val searchRequestHistoryRepository: SearchRequestHistoryRepository) :
    ViewModel() {

    val searchRequestHistory: MutableLiveData<Resource<List<SearchRequest>>> = MutableLiveData()

//    val searchRequestHistoryAlternative: LiveData<out Resource<List<SearchRequest>>> = liveData {
//
//        emit(Loading<List<SearchRequest>>())
//
//        try {
//
//            val searchRequests = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()
//            emit(Success(searchRequests))
//
//        } catch (e: Exception) {
//
//            emit(Failure(DataBaseError()))
//
//        }
//
//    }

    init {
        viewModelScope.launch {

            searchRequestHistory.value = Loading()

            loadSearchRequestHistory()

        }
    }

    fun onDeleteItemClicked(id: String) {

        searchRequestHistory.value = Loading((searchRequestHistory.value as? Success)?.data)

        viewModelScope.launch {
            deleteSearchRequestHistoryAsync(id).await()

            loadSearchRequestHistory()
        }
    }

    private suspend fun loadSearchRequestHistory() {
        try {

            val searchRequest =
                searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

            searchRequestHistory.value = Success(searchRequest)

        } catch (e: Exception) {

            searchRequestHistory.value = Failure(DataBaseError())

        }
    }

    private suspend fun deleteSearchRequestHistoryAsync(id: String) =
        viewModelScope.async { searchRequestHistoryRepository.delete(id) }

}
