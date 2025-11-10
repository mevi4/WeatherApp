package com.example.weatherapp.ui.calculator.presentation.state

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)