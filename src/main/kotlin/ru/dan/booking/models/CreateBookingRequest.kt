package ru.dan.booking.models

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class CreateBookingRequest(
    val autoSelect: Boolean,
    val roomId: Long? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate
)