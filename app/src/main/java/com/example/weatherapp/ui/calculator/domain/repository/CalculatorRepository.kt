package com.example.weatherapp.ui.calculator.domain.repository

import com.example.weatherapp.ui.calculator.domain.model.CalculationResult

interface CalculatorRepository {
    suspend fun calculate(expression: String): CalculationResult
}