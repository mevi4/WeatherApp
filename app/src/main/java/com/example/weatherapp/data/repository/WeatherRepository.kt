package com.example.weatherapp.data.repository

import com.example.weatherapp.ui.weather.model.WeatherResponse
import com.example.weatherapp.ui.weather.api.WeatherApi

class WeatherRepository {

    suspend fun getMoscowWeather(): Result<WeatherResponse> {
        return try {
            val response = WeatherApi.service.getWeather(
                latitude = 55.7558, // Москва
                longitude = 37.6173, // Москва
                currentWeather = true,
                daily = "weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
                timezone = "auto"
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка получения данных о погоде"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }
}