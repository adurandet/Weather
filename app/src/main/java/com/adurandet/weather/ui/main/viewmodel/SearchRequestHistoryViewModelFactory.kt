package com.adurandet.weather.ui.main.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.adurandet.weather.repository.SearchRequestHistoryRepository

class SearchRequestHistoryViewModelFactory(
    private val searchRequestHistoryRepository: SearchRequestHistoryRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {

        return when {

            modelClass.isAssignableFrom(SearchRequestHistoryViewModel::class.java) -> SearchRequestHistoryViewModel(searchRequestHistoryRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")

        }

    }
}