package com.example.weatherapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeatherCodeConverter {

    fun getWeatherCondition(weatherCode: Int): String {
        return when {
            weatherCode == 0 -> "Солнечно"
            weatherCode in 1..3 -> "Облачно"
            weatherCode in 51..67 || weatherCode in 80..82 -> "Дождь"
            weatherCode in 71..77 || weatherCode in 85..86 -> "Снег"
            weatherCode in 95..99 -> "Гроза"
            weatherCode == 45 || weatherCode == 48 -> "Туман"
            else -> "Облачно"
        }
    }

    fun getWeatherIcon(weatherCode: Int): String {
        return when {
            weatherCode == 0 -> "☀️"
            weatherCode in 1..3 -> "☁️"
            weatherCode in 51..67 || weatherCode in 80..82 -> "🌧️"
            weatherCode in 71..77 || weatherCode in 85..86 -> "❄️"
            weatherCode in 95..99 -> "⛈️"
            weatherCode == 45 || weatherCode == 48 -> "🌫️"
            else -> "☁️"
        }
    }

    fun getRussianDayName(date: String, position: Int): String {
        return try {
            when (position) {
                0 -> "Сегодня"
                1 -> "Завтра"
                else -> {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateObj = inputFormat.parse(date)
                    val dayFormat = SimpleDateFormat("EEEE", Locale("ru", "RU"))
                    val dayName = dayFormat.format(dateObj ?: Date())
                    dayName.replaceFirstChar { it.uppercase() }
                }
            }
        } catch (e: Exception) {
            "День"
        }
    }

    fun getRussianShortDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = inputFormat.parse(date)
            val outputFormat = SimpleDateFormat("dd MMM", Locale("ru", "RU"))
            outputFormat.format(dateObj ?: Date())
        } catch (e: Exception) {
            date
        }
    }
}