package com.example.weatherapp.ui.weather.model

data class WeatherForecast(
    val dt: Long,
    val main: MainData,
    val weather: List<Weather>,
    val wind: Wind
)