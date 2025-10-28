package ru.dan.booking.models

import java.time.LocalDate
import java.time.LocalDateTime

data class BookingDto(
    val id: Long? = null,
    val userId: Long,
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: String = "ACTIVE",
    val createdAt: LocalDateTime = LocalDateTime.now()
)