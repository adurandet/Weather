package com.adurandet.weather.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "search_request")
data class SearchRequest constructor(
    // I use the city name as UUID, a better approach would be to use a unique ID,
    // but due to the bug that the api search request by zip code return id = 0 and the the context of this simple exercise, city name is used.
    @PrimaryKey @ColumnInfo(name = "city_name") var cityName: String = "",
    @Ignore val lat: Double? = null,
    @Ignore val long: Double? = null,
    @Ignore val zipCode: String? = null) {

    @Ignore constructor( cityName: String = ""): this( cityName, null, null, null)

    @ColumnInfo(name = "created_at") var createdAt: Long = 0L
}