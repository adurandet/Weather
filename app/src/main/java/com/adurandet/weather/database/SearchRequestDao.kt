package com.adurandet.weather.database

import androidx.room.*
import com.adurandet.weather.model.SearchRequest

@Dao
interface SearchRequestDao {

    @Query("SELECT * FROM search_request ORDER BY created_at DESC")
    fun getAll(): List<SearchRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchRequest: SearchRequest)

    @Query("DELETE FROM search_request WHERE id = :id")
    fun delete(id: String)

}