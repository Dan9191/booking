package ru.dan.booking.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.dan.booking.models.BookingDto
import java.time.LocalDate
import java.time.LocalDateTime

@Table("bookings")
data class Booking(
    @Id
    val id: Long? = null,
    val userId: Long,
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: String = "ACTIVE",
    val createdAt: LocalDateTime = LocalDateTime.now()

)  {
    fun toDto(): BookingDto = BookingDto(
        userId = userId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        status = status,
        createdAt = createdAt
    )
}