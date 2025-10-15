package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.WeatherCodeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeeklyWeatherAdapter : RecyclerView.Adapter<WeeklyWeatherAdapter.WeeklyWeatherViewHolder>() {

    private var dailyData: List<DailyWeather> = emptyList()

    data class DailyWeather(
        val time: String,
        val tempMax: Double,
        val tempMin: Double,
        val weatherCode: Int,
        val precipitation: Int
    )

    inner class WeeklyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val precipitationTextView: TextView = itemView.findViewById(R.id.precipitationTextView)
        private val weatherIcon: TextView = itemView.findViewById(R.id.weatherIcon)
        private val cardView: CardView = itemView.findViewById(R.id.weatherCard)

        fun bind(weather: DailyWeather, position: Int) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputDayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val outputDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

                val date = inputFormat.parse(weather.time)
                val dayName = date?.let { outputDayFormat.format(it) } ?: "Unknown"
                val formattedDate = date?.let { outputDateFormat.format(it) } ?: weather.time

                if (position == 0) {
                    dayTextView.text = "Today"
                } else if (position == 1) {
                    dayTextView.text = "Tomorrow"
                } else {
                    dayTextView.text = dayName
                }

                dateTextView.text = formattedDate
                tempTextView.text = "${weather.tempMax.toInt()}°"

                val weatherCondition = WeatherCodeConverter.getWeatherCondition(weather.weatherCode)
                descriptionTextView.text = weatherCondition

                precipitationTextView.text = "${weather.precipitation}%"
                weatherIcon.text = WeatherCodeConverter.getWeatherIcon(weather.weatherCode)

                setupCardAppearance(weatherCondition)
            } catch (e: Exception) {
                dayTextView.text = "Error"
                dateTextView.text = weather.time
                tempTextView.text = "${weather.tempMax.toInt()}°"
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_weather, parent, false)
        return WeeklyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyWeatherViewHolder, position: Int) {
        holder.bind(dailyData[position], position)
    }

    override fun getItemCount(): Int = dailyData.size

    fun submitList(list: List<DailyWeather>) {
        dailyData = list
        notifyDataSetChanged()
    }
}