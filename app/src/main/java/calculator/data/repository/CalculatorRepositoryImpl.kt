package calculator.data.repository

import calculator.domain.model.CalculationResult
import calculator.domain.repository.CalculatorRepository
import net.objecthunter.exp4j.ExpressionBuilder


class CalculatorRepositoryImpl : CalculatorRepository {
    override suspend fun calculate(expression: String): CalculationResult {
        return try {
            if (expression.isBlank()) {
                return CalculationResult(0.0)
            }

            val cleanExpression = expression
                .replace("×", "*")
                .replace("÷", "/")

            val result = ExpressionBuilder(cleanExpression)
                .build()
                .evaluate()

            CalculationResult(result)
        } catch (e: Exception) {
            CalculationResult(0.0, "Invalid expression")
        }
    }
}