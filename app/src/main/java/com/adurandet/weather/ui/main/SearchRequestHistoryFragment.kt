package com.adurandet.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adurandet.weather.R
import com.adurandet.weather.component.SwipeToDeleteCallback
import com.adurandet.weather.database.AppDataBase
import com.adurandet.weather.model.*
import com.adurandet.weather.repository.*
import com.adurandet.weather.ui.main.adapter.WeatherSearchHistoryAdapter
import com.adurandet.weather.ui.main.viewmodel.SearchRequestHistoryViewModel
import com.adurandet.weather.ui.main.viewmodel.SearchRequestHistoryViewModelFactory
import com.adurandet.weather.ui.main.viewmodel.SharedWeatherViewModel
import com.adurandet.weather.utils.showError
import kotlinx.android.synthetic.main.search_request_history_fragment.*
import kotlinx.android.synthetic.main.search_request_history_fragment.view.*

class SearchRequestHistoryFragment : Fragment() {

    private val searchRequestDao by lazy {
        AppDataBase.getInstance(requireContext()).searchRequestDao()
    }

    private val searchRequestHistoryRepository by lazy {
        SearchRequestHistoryRepository(searchRequestDao)
    }

    private val searchRequestHistoryViewModel: SearchRequestHistoryViewModel by viewModels {
        SearchRequestHistoryViewModelFactory(searchRequestHistoryRepository, this)
    }

    private val sharedViewModel: SharedWeatherViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SharedWeatherViewModel::class.java)
    }

    private lateinit var searchHistoryAdapter: WeatherSearchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.search_request_history_fragment, container, false)

        initViews(view)

        return view
    }

    private fun initViews(view: View) {

        searchHistoryAdapter = WeatherSearchHistoryAdapter { weatherId ->
            findNavController().navigateUp()
            sharedViewModel.setSearchRequestToLoad(weatherId)
        }
        
        val swipeHandler = object : SwipeToDeleteCallback(view.context) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val searchRequest = searchHistoryAdapter.currentList[viewHolder.adapterPosition]
                searchRequestHistoryViewModel.onDeleteItemClicked(searchRequest.id)
            }

        }

        view.history_fragment_search_request_rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = searchHistoryAdapter
            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchRequestHistoryViewModel.searchRequestHistory.observe(this, Observer {
            processSearchRequestHistoryResult(it)

        })
    }

    private fun processSearchRequestHistoryResult(searchRequestHistoryResponse: Resource<List<SearchRequest>?>) {

        with(searchRequestHistoryResponse) {
            processSearchRequestHistoryLoading(this is Loading)
            when (this) {
                is Success -> processSearchRequestHistorySuccess(data)

                is Failure -> processSearchRequestHistoryError(codeError)
            }
        }

    }

    private fun processSearchRequestHistorySuccess(data: List<SearchRequest>?) {
        data?.let {
            searchHistoryAdapter.submitList(it)
            history_fragment_search_request_rv.isVisible = it.isNotEmpty()
            history_fragment_empty_view_tv.isVisible = it.isEmpty()

        }
    }

    private fun processSearchRequestHistoryLoading(showWeatherLoader: Boolean) {

        if (showWeatherLoader)
            history_fragment_progress_bar.show()
        else
            history_fragment_progress_bar.hide()

    }

    private fun processSearchRequestHistoryError(codeError: CodeError) {
        val message = when (codeError) {
            is CallError -> codeError.message
            is BadRequestError -> getString(R.string.wrong_search_request)
            is DataNotFoundError -> getString(R.string.weather_not_found)
            is DataBaseError -> getString(R.string.data_base_error)
        }
        showError(message)
    }
}