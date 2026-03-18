package com.example.weatherapp.domain.use_case

import com.example.weatherapp.data.repository.WeatherRepository

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(): Result<com.example.weatherapp.ui.weather.model.WeatherResponse> {
        return weatherRepository.getMoscowWeather()
    }
}