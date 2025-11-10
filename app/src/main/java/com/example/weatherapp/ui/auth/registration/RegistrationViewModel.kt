package com.example.weatherapp.ui.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.use_case.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword)
    }

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun register() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Валидация
            if (_state.value.password != _state.value.confirmPassword) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Пароли не совпадают"
                )
                return@launch
            }

            if (_state.value.password.length < 6) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Пароль должен содержать минимум 6 символов"
                )
                return@launch
            }

            val result = registerUseCase(
                email = _state.value.email,
                password = _state.value.password,
                name = _state.value.name
            )

            _state.value = _state.value.copy(
                isLoading = false,
                isSuccess = result,
                error = if (!result) "Пользователь с таким email уже существует" else null
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}