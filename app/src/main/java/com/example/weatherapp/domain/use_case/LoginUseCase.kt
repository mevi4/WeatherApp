package com.example.weatherapp.domain.use_case

import com.example.weatherapp.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean {
        return userRepository.loginUser(email, password).getOrNull() ?: false
    }
}