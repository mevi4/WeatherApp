package com.example.weatherapp.ui.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.databinding.FragmentLoginBinding
import com.example.weatherapp.factory.ViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(AppDatabase.getInstance(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeState()
    }

    private fun setupUI() {
        binding.apply {
            // Слушатели изменений текста
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onEmailChange(s.toString())
                }
            })

            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.onPasswordChange(s.toString())
                }
            })

            // Кнопка входа
            loginButton.setOnClickListener {
                viewModel.login()
            }

            // Переход к регистрации
            registerTextView.setOnClickListener {
                findNavController().navigate(R.id.registrationFragment)
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

    private fun updateUI(state: LoginState) {
        binding.apply {
            // Загрузка
            loginButton.isEnabled = !state.isLoading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Ошибки
            if (state.error != null) {
                errorTextView.text = state.error
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }

            // Успешная авторизация
            if (state.isSuccess) {
                Log.d("LoginFragment", "Login successful, navigating to weather")

                // Простой переход по ID
                try {
                    findNavController().navigate(R.id.weatherFragment)
                } catch (e: Exception) {
                    Log.e("LoginFragment", "Navigation error: ${e.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}