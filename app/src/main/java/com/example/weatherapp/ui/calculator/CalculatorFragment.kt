package com.example.weatherapp.ui.calculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.FragmentCalculatorBinding
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalculator()
    }

    private fun setupCalculator() {
        // Number buttons
        binding.button0.setOnClickListener { appendToDisplay("0") }
        binding.button1.setOnClickListener { appendToDisplay("1") }
        binding.button2.setOnClickListener { appendToDisplay("2") }
        binding.button3.setOnClickListener { appendToDisplay("3") }
        binding.button4.setOnClickListener { appendToDisplay("4") }
        binding.button5.setOnClickListener { appendToDisplay("5") }
        binding.button6.setOnClickListener { appendToDisplay("6") }
        binding.button7.setOnClickListener { appendToDisplay("7") }
        binding.button8.setOnClickListener { appendToDisplay("8") }
        binding.button9.setOnClickListener { appendToDisplay("9") }

        // Operation buttons
        binding.addButton.setOnClickListener { appendToDisplay("+") }
        binding.subtractButton.setOnClickListener { appendToDisplay("-") }
        binding.multiplyButton.setOnClickListener { appendToDisplay("*") }
        binding.divideButton.setOnClickListener { appendToDisplay("/") }
        binding.decimalButton.setOnClickListener { appendToDisplay(".") }

        // Function buttons
        binding.clearButton.setOnClickListener { clearDisplay() }
        binding.backspaceButton.setOnClickListener { backspace() }
        binding.equalsButton.setOnClickListener { calculate() }
    }

    private fun appendToDisplay(value: String) {
        val currentText = binding.displayEditText.text.toString()
        if (currentText == "0" || currentText == "Error") {
            binding.displayEditText.setText(value)
        } else {
            binding.displayEditText.setText(currentText + value)
        }
    }

    private fun clearDisplay() {
        binding.displayEditText.setText("0")
    }

    private fun backspace() {
        val currentText = binding.displayEditText.text.toString()
        if (currentText.length > 1) {
            binding.displayEditText.setText(currentText.substring(0, currentText.length - 1))
        } else {
            binding.displayEditText.setText("0")
        }
    }

    private fun calculate() {
        try {
            val expression = ExpressionBuilder(binding.displayEditText.text.toString()).build()
            val result = expression.evaluate()

            // Проверяем, является ли результат целым числом
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                binding.displayEditText.setText(longResult.toString())
            } else {
                binding.displayEditText.setText(result.toString())
            }
        } catch (e: Exception) {
            binding.displayEditText.setText("Error")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}