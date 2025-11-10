package com.example.weatherapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.data.repository.UserRepositoryImpl
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.use_case.GetWeatherUseCase
import com.example.weatherapp.domain.use_case.LoginUseCase
import com.example.weatherapp.domain.use_case.RegisterUseCase
import com.example.weatherapp.ui.auth.login.LoginViewModel
import com.example.weatherapp.ui.auth.registration.RegistrationViewModel
import com.example.weatherapp.ui.weather.WeatherViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                val repository = UserRepositoryImpl(appDatabase)
                val registerUseCase = RegisterUseCase(repository)
                RegistrationViewModel(registerUseCase) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val repository = UserRepositoryImpl(appDatabase)
                val loginUseCase = LoginUseCase(repository)
                LoginViewModel(loginUseCase) as T
            }
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> {
                val weatherRepository = WeatherRepository()
                val getWeatherUseCase = GetWeatherUseCase(weatherRepository)
                WeatherViewModel(getWeatherUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}