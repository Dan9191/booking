package ru.dan.booking.models

data class UserDto(
    val id: Long? = null,
    val username: String,
    val email: String,
    val role: String
)