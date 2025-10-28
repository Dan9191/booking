package ru.dan.booking.models

data class RoomDto(
    val id: Long? = null,
    val hotelId: Long,
    val number: String
)