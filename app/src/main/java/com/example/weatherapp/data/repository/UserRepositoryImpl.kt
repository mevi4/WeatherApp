package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.local.database.AppDatabase
import com.example.weatherapp.data.local.database.entity.UserEntity
import com.example.weatherapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val database: AppDatabase
) : UserRepository {

    private val userDao = database.userDao()
    private val TAG = "UserRepository"

    override suspend fun registerUser(email: String, password: String, name: String): Result<Boolean> {
        return try {
            Log.d(TAG, "Registering user: $email")

            // Проверяем, нет ли уже пользователя с таким email
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Log.d(TAG, "User already exists: $email")
                Result.failure(Exception("Пользователь с email $email уже существует"))
            } else {
                val user = UserEntity(
                    email = email,
                    password = password,
                    name = name
                )
                userDao.insertUser(user)
                Log.d(TAG, "User registered successfully: $email")
                Result.success(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error: ${e.message}")
            Result.failure(Exception("Ошибка регистрации: ${e.message}"))
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Boolean> {
        return try {
            Log.d(TAG, "Login attempt: $email")

            val user = userDao.getUser(email, password)
            if (user != null) {
                Log.d(TAG, "Login successful: $email")
                Result.success(true)
            } else {
                Log.d(TAG, "Login failed: invalid credentials for $email")
                Result.failure(Exception("Неверный email или пароль"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}")
            Result.failure(Exception("Ошибка авторизации: ${e.message}"))
        }
    }

    override fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }
}