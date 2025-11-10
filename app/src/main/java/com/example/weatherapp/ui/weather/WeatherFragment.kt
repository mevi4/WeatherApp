package com.example.weatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.WeatherCodeConverter
import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.factory.ViewModelFactory
import com.example.weatherapp.ui.weather.adapter.WeeklyWeatherAdapter
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels {
        ViewModelFactory(AppDatabase.getInstance(requireContext()))
    }

    private val weeklyAdapter = WeeklyWeatherAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.weeklyForecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weeklyAdapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: WeatherState) {
        binding.apply {
            // Загрузка
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Ошибки
            if (state.error != null) {
                errorTextView.text = state.error
                errorTextView.visibility = View.VISIBLE
                weatherCard.visibility = View.GONE
                weeklyForecastRecyclerView.visibility = View.GONE
            } else {
                errorTextView.visibility = View.GONE
            }

            // Данные погоды
            state.weatherData?.let { weatherData ->
                weatherCard.visibility = View.VISIBLE
                weeklyForecastRecyclerView.visibility = View.VISIBLE

                displayCurrentWeather(weatherData)
                displayWeeklyForecast(weatherData)
            }
        }
    }

    private fun displayCurrentWeather(weatherData: com.example.weatherapp.ui.weather.model.WeatherResponse) {
        binding.apply {
            val current = weatherData.current_weather
            val weatherCondition = WeatherCodeConverter.getWeatherCondition(current.weathercode)

            // Устанавливаем данные
            cityNameTextView.text = "Москва"
            temperatureTextView.text = "${current.temperature.toInt()}°C"
            descriptionTextView.text = weatherCondition

            // Берем влажность из первого дня недельного прогноза
            val humidity = weatherData.daily.precipitation_probability_max.firstOrNull() ?: 0
            humidityTextView.text = "Влажность: $humidity%"
            windTextView.text = "Ветер: ${current.windspeed.toInt()} м/с"

            // Меняем цвет карточки в зависимости от погоды
            setWeatherCardColor(weatherCondition)
        }
    }

    private fun setWeatherCardColor(weatherCondition: String) {
        val colorRes = when (weatherCondition.toLowerCase()) {
            "солнечно", "ясно" -> com.example.weatherapp.R.color.weather_sunny
            "облачно", "переменная облачность" -> com.example.weatherapp.R.color.weather_cloudy
            "дождь", "небольшой дождь", "умеренный дождь" -> com.example.weatherapp.R.color.weather_rainy
            "снег", "небольшой снег", "умеренный снег" -> com.example.weatherapp.R.color.weather_snow
            "гроза", "грозы" -> com.example.weatherapp.R.color.weather_thunderstorm
            "туман", "дымка" -> com.example.weatherapp.R.color.weather_foggy
            else -> com.example.weatherapp.R.color.weather_clear
        }

        binding.weatherCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun displayWeeklyForecast(weatherData: com.example.weatherapp.ui.weather.model.WeatherResponse) {
        val dailyList = mutableListOf<WeeklyWeatherAdapter.DailyWeather>()

        for (i in weatherData.daily.time.indices) {
            if (i < 7) { // Показываем только 7 дней
                dailyList.add(
                    WeeklyWeatherAdapter.DailyWeather(
                        time = weatherData.daily.time[i],
                        tempMax = weatherData.daily.temperature_2m_max[i],
                        tempMin = weatherData.daily.temperature_2m_min[i],
                        weatherCode = weatherData.daily.weathercode[i],
                        precipitation = weatherData.daily.precipitation_probability_max[i]
                    )
                )
            }
        }

        weeklyAdapter.submitList(dailyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}