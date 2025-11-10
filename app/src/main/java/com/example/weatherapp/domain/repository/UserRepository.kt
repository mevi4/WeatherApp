package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(email: String, password: String, name: String): Result<Boolean>
    suspend fun loginUser(email: String, password: String): Result<Boolean>
    fun getAllUsers(): Flow<List<UserEntity>>
}