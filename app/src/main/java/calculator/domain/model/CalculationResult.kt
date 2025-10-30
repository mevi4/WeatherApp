package calculator.domain.model

data class CalculationResult(
    val value: Double,
    val error: String? = null
)