package com.example.weatherapp.di

import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.data.repository.UserRepositoryImpl
import com.example.weatherapp.domain.repository.UserRepository
import com.example.weatherapp.domain.use_case.LoginUseCase
import com.example.weatherapp.domain.use_case.RegisterUseCase
import com.example.weatherapp.ui.auth.login.LoginViewModel
import com.example.weatherapp.ui.auth.registration.RegistrationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().userDao() }

    // Repository - с явным указанием типа
    single<UserRepository> { UserRepositoryImpl(database = get()) }

    // Use Cases - с явным указанием типа
    single { RegisterUseCase(userRepository = get()) }
    single { LoginUseCase(userRepository = get()) }

    // ViewModels - с явным указанием типа
    viewModel { RegistrationViewModel(registerUseCase = get()) }
    viewModel { LoginViewModel(loginUseCase = get()) }
}