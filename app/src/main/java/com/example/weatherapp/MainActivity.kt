package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.WeeklyWeatherAdapter
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.model.WeatherResponse
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var weatherCardContainer: FrameLayout
    private lateinit var weeklyContainer: FrameLayout
    private lateinit var weatherCard: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var weeklyRecyclerView: RecyclerView
    private lateinit var weeklyAdapter: WeeklyWeatherAdapter
    private lateinit var toolbar: MaterialToolbar

    private lateinit var cityTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var weatherIcon: TextView
    private lateinit var tomorrowButton: TextView
    private lateinit var weekButton: TextView

    private val scope = CoroutineScope(Dispatchers.Main)
    private val moscowLatitude = 55.7558
    private val moscowLongitude = 37.6173
    private var weatherResponse: WeatherResponse? = null
    private var currentViewMode: ViewMode = ViewMode.TODAY

    enum class ViewMode {
        TODAY, TOMORROW, WEEK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupViews()
        setupClickListeners()
        loadWeatherData()
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViews() {
        weatherCardContainer = findViewById(R.id.weatherCardContainer)
        weeklyContainer = findViewById(R.id.weeklyContainer)
        weatherCard = findViewById(R.id.weatherCard)
        progressBar = findViewById(R.id.progressBar)
        weeklyRecyclerView = findViewById(R.id.weeklyRecyclerView)

        cityTextView = findViewById(R.id.cityTextView)
        timeTextView = findViewById(R.id.timeTextView)
        tempTextView = findViewById(R.id.tempTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        weatherIcon = findViewById(R.id.weatherIcon)
        tomorrowButton = findViewById(R.id.tomorrowButton)
        weekButton = findViewById(R.id.weekButton)

        weeklyAdapter = WeeklyWeatherAdapter()
        weeklyRecyclerView.adapter = weeklyAdapter
        weeklyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        tomorrowButton.setOnClickListener {
            showTomorrowWeather()
        }
        weekButton.setOnClickListener {
            showWeeklyWeather()
        }
    }

    private fun loadWeatherData() {
        progressBar.visibility = View.VISIBLE
        scope.launch {
            try {
                val response: WeatherResponse = withContext(Dispatchers.IO) {
                    WeatherApi.service.getWeatherForecast(
                        latitude = moscowLatitude,
                        longitude = moscowLongitude
                    )
                }
                weatherResponse = response
                showTodayWeather()
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Ошибка загрузки: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showTodayWeather() {
        weatherResponse?.let { response ->
            try {
                val currentWeather = response.current_weather
                updateWeatherCard(
                    temperature = currentWeather.temperature,
                    weatherCode = currentWeather.weathercode,
                    time = currentWeather.time,
                    isCurrentWeather = true
                )
                currentViewMode = ViewMode.TODAY
                supportActionBar?.title = "Сегодня"
                showWeatherCardView()
            } catch (e: Exception) {
                showError("Ошибка отображения: ${e.message}")
            }
        } ?: showError("Нет данных о погоде")
    }

    private fun showTomorrowWeather() {
        weatherResponse?.let { response ->
            try {
                if (response.daily.time.size > 1) {
                    val tomorrowTemp = response.daily.temperature_2m_max[1]
                    val tomorrowCode = response.daily.weathercode[1]

                    updateWeatherCard(
                        temperature = tomorrowTemp,
                        weatherCode = tomorrowCode,
                        time = response.daily.time[1],
                        isCurrentWeather = false
                    )
                    currentViewMode = ViewMode.TOMORROW
                    supportActionBar?.title = "Завтра"
                } else {
                    showError("Нет данных на завтра")
                }
            } catch (e: Exception) {
                showError("Ошибка завтра: ${e.message}")
            }
        } ?: showError("Нет данных о погоде")
    }

    private fun showWeeklyWeather() {
        weatherResponse?.let { response ->
            try {
                val weeklyList = mutableListOf<WeeklyWeatherAdapter.DailyWeather>()
                for (i in 0 until minOf(7, response.daily.time.size)) {
                    weeklyList.add(
                        WeeklyWeatherAdapter.DailyWeather(
                            time = response.daily.time[i],
                            tempMax = response.daily.temperature_2m_max[i],
                            tempMin = response.daily.temperature_2m_min[i],
                            weatherCode = response.daily.weathercode[i],
                            precipitation = response.daily.precipitation_probability_max.getOrElse(i) { 0 }
                        )
                    )
                }
                weeklyAdapter.submitList(weeklyList)
                currentViewMode = ViewMode.WEEK
                supportActionBar?.title = "Неделя"
                showWeeklyView()
                updateToolbarColor("Облачно")
            } catch (e: Exception) {
                showError("Ошибка недели: ${e.message}")
            }
        } ?: showError("Нет данных о погоде")
    }

    private fun updateWeatherCard(temperature: Double, weatherCode: Int, time: String, isCurrentWeather: Boolean) {
        cityTextView.text = "Москва"

        val formattedTime = try {
            if (isCurrentWeather) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(time)
                outputFormat.format(date ?: Date())
            } else {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                val date = inputFormat.parse(time)
                outputFormat.format(date ?: Date())
            }
        } catch (e: Exception) {
            time
        }
        timeTextView.text = formattedTime

        tempTextView.text = "${temperature.toInt()}°"

        val weatherCondition = WeatherCodeConverter.getWeatherCondition(weatherCode)
        descriptionTextView.text = weatherCondition

        weatherIcon.text = WeatherCodeConverter.getWeatherIcon(weatherCode)

        updateCardColor(weatherCondition)
        updateToolbarColor(weatherCondition)
    }

    private fun updateCardColor(weatherCondition: String) {
        val colorRes = getColorForWeather(weatherCondition)
        weatherCard.setCardBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    private fun updateToolbarColor(weatherCondition: String) {
        val colorRes = getColorForWeather(weatherCondition)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    private fun getColorForWeather(weatherCondition: String): Int {
        return when (weatherCondition) {
            "Солнечно" -> R.color.weather_sunny
            "Облачно" -> R.color.weather_cloudy
            "Дождь" -> R.color.weather_rainy
            "Гроза" -> R.color.weather_thunderstorm
            "Туман" -> R.color.weather_foggy
            "Снег" -> R.color.weather_windy
            else -> R.color.weather_cloudy
        }
    }

    private fun showWeatherCardView() {
        weatherCardContainer.visibility = View.VISIBLE
        weeklyContainer.visibility = View.GONE
    }

    private fun showWeeklyView() {
        weatherCardContainer.visibility = View.GONE
        weeklyContainer.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        when (currentViewMode) {
            ViewMode.WEEK -> showTodayWeather()
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}