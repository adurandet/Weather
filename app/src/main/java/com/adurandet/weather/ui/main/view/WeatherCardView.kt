package com.adurandet.weather.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.adurandet.weather.R
import com.adurandet.weather.model.Weather
import com.adurandet.weather.utils.roundNumber
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.weather_card_layout.view.*

class WeatherCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    init {
      View.inflate(context, R.layout.weather_card_layout, this)
    }

    fun setWeather(weather: Weather) {

        with(weather) {

            weather_card_name.text = name

            weather_card_description.text = description

            weather_card_temperature.text = context.getString(R.string.temperature, temperature.roundNumber(), context.getString(R.string.celsius))

            Glide.with(context)
                .load(iconURl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(weather_card_icon)
        }

    }

}