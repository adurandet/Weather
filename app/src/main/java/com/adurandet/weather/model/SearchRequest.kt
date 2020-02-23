package com.adurandet.weather.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "search_request")
data class SearchRequest @JvmOverloads constructor(
    @PrimaryKey var id: String = "",
    @ColumnInfo(name = "city_name") var cityName: String = "",
    @Ignore val lat: Double? = null,
    @Ignore val long: Double? = null,
    @Ignore val zipCode: String? = null,
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis()
)