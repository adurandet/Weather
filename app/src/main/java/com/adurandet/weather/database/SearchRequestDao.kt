package com.adurandet.weather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adurandet.weather.model.SearchRequest

@Dao
interface SearchRequestDao {

    @Query("SELECT * FROM search_request")
    fun getAll(): List<SearchRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchRequest(searchRequest: SearchRequest)

}