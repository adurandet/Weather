package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adurandet.weather.model.DataBaseError
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.repository.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.java.KoinJavaComponent.get

class SearchRequestHistoryViewModel(
    private val searchRequestHistoryRepository: SearchRequestHistoryRepository = get(SearchRequestHistoryRepository::class.java)
) : ViewModel(), KoinComponent {

    val searchRequestHistoryLiveData: MutableLiveData<Resource<List<SearchRequest>>> = MutableLiveData()

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

            searchRequestHistoryLiveData.value = Loading()

            loadSearchRequestHistory()

        }
    }

    fun onDeleteItemClicked(id: String) {

        searchRequestHistoryLiveData.value = Loading((searchRequestHistoryLiveData.value as? Success)?.data)

        viewModelScope.launch {
            deleteSearchRequestHistoryAsync(id).await()

            loadSearchRequestHistory()
        }
    }

    private suspend fun loadSearchRequestHistory() {
        try {

            val searchRequest =
                searchRequestHistoryRepository.getSearchRequestHistoryAsync().await()

            searchRequestHistoryLiveData.value = Success(searchRequest)

        } catch (e: Exception) {

            searchRequestHistoryLiveData.value = Failure(DataBaseError())

        }
    }

    private suspend fun deleteSearchRequestHistoryAsync(id: String) =
        viewModelScope.async { searchRequestHistoryRepository.delete(id) }

}
