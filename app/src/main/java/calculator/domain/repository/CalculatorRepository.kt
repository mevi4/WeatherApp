package calculator.domain.repository

import calculator.domain.model.CalculationResult

interface CalculatorRepository {
    suspend fun calculate(expression: String): CalculationResult
}