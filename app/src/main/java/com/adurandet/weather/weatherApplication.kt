package com.adurandet.weather

import android.app.Application
import com.adurandet.weather.database.AppDataBase
import com.adurandet.weather.network.ApiHelper
import com.adurandet.weather.repository.SearchRequestHistoryRepository
import com.adurandet.weather.repository.WeatherRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherApplication : Application() {

    private val modules = module {
        single { ApiHelper.getIntance() }

        //SearchRequestDao
        single { AppDataBase.getInstance(androidContext()).searchRequestDao() }

        single { WeatherRepository() }
        single { SearchRequestHistoryRepository() }

    }

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidLogger()

            androidContext(this@WeatherApplication)

            modules(modules)

        }

    }
}