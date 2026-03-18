package com.example.weatherapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val password: String,
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)