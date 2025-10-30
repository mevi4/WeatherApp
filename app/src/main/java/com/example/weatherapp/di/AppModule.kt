package com.example.weatherapp.di

import calculator.data.repository.CalculatorRepositoryImpl
import calculator.domain.repository.CalculatorRepository
import calculator.domain.use_case.CalculateUseCase
import calculator.presentation.viewmodel.CalculatorViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // Calculator feature
    single<CalculatorRepository> { CalculatorRepositoryImpl() }
    single { CalculateUseCase(get()) }
    viewModel { CalculatorViewModel(get()) }

    // Retrofit for Weather API
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
}