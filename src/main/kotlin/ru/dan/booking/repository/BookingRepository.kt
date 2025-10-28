package ru.dan.booking.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.dan.booking.entity.Booking

interface BookingRepository : ReactiveCrudRepository<Booking, Long> {
    abstract fun findByUserId(userId: Long): Flux<Booking>
}