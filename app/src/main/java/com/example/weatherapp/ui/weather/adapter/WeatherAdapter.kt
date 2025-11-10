package com.example.weatherapp.ui.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.WeatherCodeConverter

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    private var weatherData: WeatherData? = null

    data class WeatherData(
        val temperature: Double,
        val weatherCode: Int,
        val description: String
    )

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val additionalInfoTextView: TextView = itemView.findViewById(R.id.additionalInfoTextView)
        private val weatherIcon: TextView = itemView.findViewById(R.id.weatherIcon)
        private val cardView: CardView = itemView.findViewById(R.id.weatherCard)

        fun bind(weather: WeatherData) {
            cityTextView.text = "Moscow"
            tempTextView.text = "${weather.temperature.toInt()}°"
            descriptionTextView.text = weather.description
            additionalInfoTextView.text = "Mostly cloudy"
            weatherIcon.text = WeatherCodeConverter.getWeatherIcon(weather.weatherCode)
            setupCardAppearance(weather.description)
        }

        private fun setupCardAppearance(weatherCondition: String) {
            val colorRes = when (weatherCondition) {
                "Солнечно" -> R.color.weather_sunny
                "Облачно" -> R.color.weather_cloudy
                "Дождь" -> R.color.weather_rainy
                "Гроза" -> R.color.weather_thunderstorm
                "Туман" -> R.color.weather_foggy
                "Снег" -> R.color.weather_windy
                else -> R.color.weather_cloudy
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, colorRes))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        weatherData?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (weatherData != null) 1 else 0
}