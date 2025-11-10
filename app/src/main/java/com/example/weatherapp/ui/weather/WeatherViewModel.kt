package com.example.weatherapp.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.use_case.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = getWeatherUseCase()

            _state.value = _state.value.copy(
                isLoading = false,
                weatherData = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun refreshWeather() {
        loadWeather()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class WeatherState(
    val isLoading: Boolean = false,
    val weatherData: com.example.weatherapp.ui.weather.model.WeatherResponse? = null,
    val error: String? = null
)