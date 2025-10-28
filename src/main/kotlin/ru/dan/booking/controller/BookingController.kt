package ru.dan.booking.controller

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.dan.booking.models.BookingDto
import ru.dan.booking.models.CreateBookingRequest
import ru.dan.booking.service.BookingService

@RestController
@RequestMapping("/api/booking")
class BookingController(
    private val bookingService: BookingService,
    @Qualifier("gatewayWebClient") private val webClient: WebClient
) {

    /**
     * Информация о бронировании.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BOOKING_SERVICE', 'ROLE_ADMIN', 'ROLE_USER')")
    fun getBooking(@PathVariable id: Long, @RequestHeader("Authorization") token: String): Mono<BookingDto> {
        return bookingService.getBooking(id, token)
    }

    /**
     * Отменна брони.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BOOKING_SERVICE', 'ROLE_ADMIN', 'ROLE_USER')")
    fun cancelBooking(@PathVariable id: Long, @RequestHeader("Authorization") token: String): Mono<Void> {
        return bookingService.cancelBooking(id, token)
    }

    /**
     *
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_BOOKING_SERVICE', 'ROLE_ADMIN', 'ROLE_USER')")
    fun getUserBookings(@RequestHeader("Authorization") token: String): Flux<BookingDto> {
        return bookingService.getUserBookings(token)
    }

    /**
     * Создать бронь.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_BOOKING_SERVICE', 'ROLE_ADMIN', 'ROLE_USER')")
    fun createBooking(@RequestBody request: CreateBookingRequest,
                      @RequestHeader("Authorization") token: String,
                      @RequestHeader("X-Correlation-Id") correlationId: String): Mono<BookingDto> {
        return bookingService.createBooking(request, token, correlationId)
    }

    /**
     * Тестовый метод.
     * Вызывает Hotel service и возвращает его ответ + привет от Booking.
     */
    @GetMapping("/hi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun hi(
        @RequestHeader("X-Correlation-Id") correlationId: String,
        @RequestHeader("Authorization") authHeader: String
    ): Mono<String> {
        return webClient.get()
            .uri("/api/hotels/hi")
            .header("Authorization", authHeader)
            .header("X-Correlation-Id", correlationId)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { hotelResponse ->
                "$hotelResponse | Booking service is alive too! [cid: $correlationId]"
            }
            .onErrorResume { error ->
                Mono.just("Hotel service unreachable: ${error.message} | But Booking is alive! [cid: $correlationId]")
            }
    }
}