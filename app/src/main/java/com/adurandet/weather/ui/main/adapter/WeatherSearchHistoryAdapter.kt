package com.adurandet.weather.ui.main.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adurandet.weather.R
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.utils.inflate
import kotlinx.android.synthetic.main.list_item_search_request_layout.view.*

class WeatherSearchHistoryAdapter(
    private val onItemClickListener: (itemId: String) -> Unit
) :
    ListAdapter<SearchRequest, WeatherSearchHistoryAdapter.SearchRequestViewHolder>(DiffUtilLaunchItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRequestViewHolder {
        val view = parent.inflate(R.layout.list_item_search_request_layout)
        return SearchRequestViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: SearchRequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchRequestViewHolder(itemView: View,
                                  private val onItemClickListener: (itemId: String) -> Unit
                                  ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SearchRequest) {
            itemView.list_item_search_request_name.text = item.cityName
            itemView.setOnClickListener { onItemClickListener(item.id) }
        }

    }

}

class DiffUtilLaunchItemCallback : DiffUtil.ItemCallback<SearchRequest>() {
    override fun areItemsTheSame(oldItem: SearchRequest, newItem: SearchRequest) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: SearchRequest, newItem: SearchRequest) = oldItem == newItem
}