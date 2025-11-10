package com.example.weatherapp.ui.auth.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.databinding.FragmentRegistrationBinding
import com.example.weatherapp.factory.ViewModelFactory
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    // Получаем ViewModel через фабрику
    private val viewModel: RegistrationViewModel by viewModels {
        ViewModelFactory(AppDatabase.getInstance(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeState()
    }

    private fun setupUI() {
        binding.apply {
            // Слушатели изменений текста для Email
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onEmailChange(s.toString())
                }
            })

            // Слушатели изменений текста для Пароля
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onPasswordChange(s.toString())
                }
            })

            // Слушатели изменений текста для Подтверждения пароля
            confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onConfirmPasswordChange(s.toString())
                }
            })

            // Слушатели изменений текста для Имени
            nameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onNameChange(s.toString())
                }
            })

            // Кнопка регистрации
            registerButton.setOnClickListener {
                viewModel.register()
            }

            // Переход к логину
            loginTextView.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: RegistrationState) {
        binding.apply {
            // Загрузка
            registerButton.isEnabled = !state.isLoading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Ошибки
            if (state.error != null) {
                errorTextView.text = state.error
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }

            // Успешная регистрация
            if (state.isSuccess) {
                // Переход к основному экрану
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}