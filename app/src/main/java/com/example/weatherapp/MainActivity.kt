package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calculator.presentation.ui.CalculatorScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                WeatherApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    var currentScreenIndex by rememberSaveable { mutableIntStateOf(0) }

    val currentScreen = when (currentScreenIndex) {
        0 -> Screen.Weather
        1 -> Screen.Calculator
        else -> Screen.Weather
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getScreenTitle(currentScreen)) },
                navigationIcon = {
                    if (currentScreen != Screen.Weather) {
                        IconButton(onClick = { currentScreenIndex = 0 }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_revert),
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_weather),
                            contentDescription = "Weather",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Погода") },
                    selected = currentScreen == Screen.Weather,
                    onClick = { currentScreenIndex = 0 }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_calculator),
                            contentDescription = "Calculator",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Калькулятор") },
                    selected = currentScreen == Screen.Calculator,
                    onClick = { currentScreenIndex = 1 }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            Screen.Weather -> WeatherScreen(modifier = Modifier.padding(innerPadding))
            Screen.Calculator -> CalculatorScreen()
        }
    }
}

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var weatherView by remember { mutableStateOf(WeatherView.TODAY) }

    // Данные для разных периодов
    var todayWeather by remember { mutableStateOf<WeatherData?>(null) }
    var tomorrowWeather by remember { mutableStateOf<WeatherData?>(null) }
    var weeklyData by remember { mutableStateOf<List<DailyWeather>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        // Имитация загрузки данных
        try {
            // Сегодняшняя погода
            todayWeather = WeatherData(
                city = "Москва",
                time = "Сегодня, 15:30",
                temperature = 20.0,
                description = "Облачно",
                humidity = 65,
                windSpeed = 5.0,
                feelsLike = 19.0,
                pressure = 1013,
                visibility = 10.0
            )

            // Завтрашняя погода
            tomorrowWeather = WeatherData(
                city = "Москва",
                time = "Завтра",
                temperature = 18.0,
                description = "Дождь",
                humidity = 80,
                windSpeed = 7.0,
                feelsLike = 16.0,
                pressure = 1005,
                visibility = 5.0
            )

            // Недельный прогноз
            weeklyData = listOf(
                DailyWeather("Понедельник", 22.0, 15.0, "Солнечно", 10),
                DailyWeather("Вторник", 20.0, 14.0, "Облачно", 30),
                DailyWeather("Среда", 18.0, 12.0, "Дождь", 80),
                DailyWeather("Четверг", 19.0, 13.0, "Облачно", 40),
                DailyWeather("Пятница", 21.0, 14.0, "Солнечно", 5),
                DailyWeather("Суббота", 23.0, 16.0, "Ясно", 0),
                DailyWeather("Воскресенье", 24.0, 17.0, "Ясно", 0)
            )
        } catch (e: Exception) {
            error = "Ошибка загрузки: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Кнопки переключения вида погоды
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherViewButton(
                text = "Сегодня",
                isSelected = weatherView == WeatherView.TODAY,
                onClick = { weatherView = WeatherView.TODAY },
                modifier = Modifier.weight(1f)
            )
            WeatherViewButton(
                text = "Завтра",
                isSelected = weatherView == WeatherView.TOMORROW,
                onClick = { weatherView = WeatherView.TOMORROW },
                modifier = Modifier.weight(1f)
            )
            WeatherViewButton(
                text = "Неделя",
                isSelected = weatherView == WeatherView.WEEK,
                onClick = { weatherView = WeatherView.WEEK },
                modifier = Modifier.weight(1f)
            )
        }

        // Контент в зависимости от выбранного вида
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 32.dp)
                )
            }
            error != null -> {
                ErrorContent(
                    errorMessage = error!!,
                    onRetry = {
                        error = null
                        isLoading = true
                        // Перезагрузка данных
                    }
                )
            }
            else -> {
                when (weatherView) {
                    WeatherView.TODAY -> {
                        todayWeather?.let { weatherData ->
                            TodayWeatherContent(weatherData = weatherData)
                        }
                    }
                    WeatherView.TOMORROW -> {
                        tomorrowWeather?.let { weatherData ->
                            TodayWeatherContent(weatherData = weatherData)
                        }
                    }
                    WeatherView.WEEK -> {
                        WeeklyWeatherContent(weeklyData = weeklyData)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherViewButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun TodayWeatherContent(weatherData: WeatherData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Основная информация о погоде
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weatherData.city,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = weatherData.time,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${weatherData.temperature.toInt()}°",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = weatherData.description,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ощущается как ${weatherData.feelsLike.toInt()}°",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Дополнительная информация
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Детали",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(
                        title = "Влажность",
                        value = "${weatherData.humidity}%",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherDetailItem(
                        title = "Ветер",
                        value = "${weatherData.windSpeed} м/с",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(
                        title = "Давление",
                        value = "${weatherData.pressure} гПа",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherDetailItem(
                        title = "Видимость",
                        value = "${weatherData.visibility} км",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeeklyWeatherContent(weeklyData: List<DailyWeather>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Прогноз на 7 дней",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weeklyData) { daily ->
                DailyWeatherCard(dailyWeather = daily)
            }
        }
    }
}

@Composable
fun DailyWeatherCard(dailyWeather: DailyWeather) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = dailyWeather.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dailyWeather.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${dailyWeather.tempMax.toInt()}° / ${dailyWeather.tempMin.toInt()}°",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${dailyWeather.precipitation}%",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}

@Composable
fun ErrorContent(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка загрузки",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

private fun getScreenTitle(screen: Screen): String {
    return when (screen) {
        Screen.Weather -> "Погода"
        Screen.Calculator -> "Калькулятор"
    }
}

enum class Screen {
    Weather, Calculator
}

enum class WeatherView {
    TODAY, TOMORROW, WEEK
}

// Обновленная модель данных с дополнительными полями
data class WeatherData(
    val city: String,
    val time: String,
    val temperature: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val feelsLike: Double,
    val pressure: Int,
    val visibility: Double
)

data class DailyWeather(
    val date: String,
    val tempMax: Double,
    val tempMin: Double,
    val description: String,
    val precipitation: Int
)