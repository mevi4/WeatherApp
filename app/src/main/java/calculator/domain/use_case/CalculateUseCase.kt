package calculator.domain.use_case

import calculator.domain.model.CalculationResult
import calculator.domain.repository.CalculatorRepository

class CalculateUseCase(
    private val repository: CalculatorRepository
) {
    suspend operator fun invoke(expression: String): CalculationResult {
        return repository.calculate(expression)
    }
}