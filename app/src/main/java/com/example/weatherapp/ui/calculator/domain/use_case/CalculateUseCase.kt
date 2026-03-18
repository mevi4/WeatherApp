package com.example.weatherapp.ui.calculator.domain.use_case

import com.example.weatherapp.ui.calculator.domain.model.CalculationResult
import com.example.weatherapp.ui.calculator.domain.repository.CalculatorRepository

class CalculateUseCase(
    private val repository: CalculatorRepository
) {
    suspend operator fun invoke(expression: String): CalculationResult {
        return repository.calculate(expression)
    }
}