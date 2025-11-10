package com.example.weatherapp.ui.calculator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.ui.calculator.domain.use_case.CalculateUseCase
import com.example.weatherapp.ui.calculator.presentation.state.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val calculateUseCase: CalculateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onExpressionChange(expression: String) {
        _state.update { it.copy(expression = expression) }
    }

    fun calculate() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = calculateUseCase(_state.value.expression)

            _state.update {
                it.copy(
                    isLoading = false,
                    result = if (result.error != null) {
                        result.error
                    } else {
                        result.value.toString()
                    },
                    error = result.error
                )
            }
        }
    }

    fun clear() {
        _state.update {
            CalculatorState()
        }
    }

    fun appendToExpression(value: String) {
        _state.update {
            it.copy(expression = it.expression + value)
        }
    }
}