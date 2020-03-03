package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adurandet.weather.model.DataBaseError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.*
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchRequestHistoryViewModel : ViewModel(), KoinComponent {

    private val searchRequestHistoryRepository: SearchRequestHistoryRepository by inject()

    val searchRequestHistory: MutableLiveData<Resource<List<SearchRequest>>> = MutableLiveData()

    init {
        viewModelScope.launch {

            searchRequestHistory.value = Loading()

            try {

                val searchRequest = searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

                searchRequestHistory.value = Success(searchRequest)

            }catch (e: Exception){

                searchRequestHistory.value = Failure(DataBaseError())

            }

        }

    }

}
