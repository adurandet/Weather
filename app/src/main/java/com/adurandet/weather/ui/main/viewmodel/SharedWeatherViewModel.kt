package com.adurandet.weather.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedWeatherViewModel : ViewModel() {

    var loadSearchRequestLiveData = MutableLiveData<String>()

    fun setSearchRequestToLoad(id: String) {
        loadSearchRequestLiveData.value = id
    }
}
