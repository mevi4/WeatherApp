package com.example.weatherapp.model

import com.example.weatherapp.model.MainData
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.Wind

data class WeatherForecast(
    val dt: Long,
    val main: MainData,
    val weather: List<Weather>,
    val wind: Wind
)