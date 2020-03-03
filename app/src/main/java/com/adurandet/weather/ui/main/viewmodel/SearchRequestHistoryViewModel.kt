package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import androidx.lifecycle.*
import com.adurandet.weather.model.DataBaseError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.*
import kotlinx.coroutines.launch

class SearchRequestHistoryViewModel(searchRequestHistoryRepository: SearchRequestHistoryRepository) : ViewModel() {

    val searchRequestHistory: MutableLiveData<Resource<List<SearchRequest>>> = MutableLiveData()

    init {
        viewModelScope.launch {

            searchRequestHistory.value = Loading()

            try {

                val searchRequest =
                    searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()
                searchRequestHistory.value = Success(searchRequest)

            } catch (e: Exception) {

                searchRequestHistory.value = Failure(DataBaseError())

            }
        }
    }

    val searchRequestHistoryAlternative: LiveData<out Resource<List<SearchRequest>>> = liveData {

        emit(Loading<List<SearchRequest>>())

        try {

            val searchRequests = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()
            emit(Success(searchRequests))

        } catch (e: Exception) {

            emit(Failure(DataBaseError()))

        }

    }

}
