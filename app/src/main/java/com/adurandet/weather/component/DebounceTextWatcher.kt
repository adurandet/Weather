package com.adurandet.weather.component

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class DebounceTextWatcher : TextWatcher, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var search: String = ""

    override fun afterTextChanged(s: Editable?) {
        val searchTemp = s.toString().trim()
        if (search == searchTemp)
            return

        search = searchTemp

        launch {
            delay(300)  //debounce timeOut
            if (search != searchTemp)
                return@launch

            afterDebounceTextChanged(search)
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    abstract fun afterDebounceTextChanged(s: String)

}