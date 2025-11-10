package com.example.weatherapp.domain.use_case

import com.example.weatherapp.domain.repository.UserRepository

class RegisterUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Boolean {
        return userRepository.registerUser(email, password, name).getOrNull() ?: false
    }
}