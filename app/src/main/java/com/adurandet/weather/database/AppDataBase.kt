package com.adurandet.weather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adurandet.weather.model.SearchRequest
import com.adurandet.weather.utils.DATABASE_NAME

@Database(entities = [SearchRequest::class], version = 2, exportSchema = false )
abstract class AppDataBase : RoomDatabase() {

    abstract fun searchRequestDao(): SearchRequestDao

    companion object {

        @Volatile private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
                // I am using a simple fallback destructive migration in the context of this exercise
                // but a real migration plan should be added to keep data.
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}