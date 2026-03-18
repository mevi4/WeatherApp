package com.example.weatherapp.ui.calculator.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.ui.calculator.presentation.viewmodel.CalculatorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Calculator",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = state.expression,
            onValueChange = viewModel::onExpressionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter expression") }
        )

        // Number buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("7", "8", "9", "/").forEach { value ->
                    CalculatorButton(value) { viewModel.appendToExpression(value) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("4", "5", "6", "*").forEach { value ->
                    CalculatorButton(value) { viewModel.appendToExpression(value) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1", "2", "3", "-").forEach { value ->
                    CalculatorButton(value) { viewModel.appendToExpression(value) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("0", ".", "=", "+").forEach { value ->
                    CalculatorButton(value) {
                        if (value == "=") {
                            viewModel.calculate()
                        } else {
                            viewModel.appendToExpression(value)
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = viewModel::calculate,
                modifier = Modifier.weight(1f)
            ) {
                Text("Calculate")
            }

            Button(
                onClick = viewModel::clear,
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.result.isNotBlank()) {
            Text(
                text = "Result: ${state.result}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(60.dp)
    ) {
        Text(text)
    }
}