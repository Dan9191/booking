package ru.dan.booking.service

import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.dan.booking.entity.Booking
import ru.dan.booking.models.*
import ru.dan.booking.repository.BookingRepository
import ru.dan.hotel.model.HotelDto
import java.time.LocalDate
import java.util.*


@Service
class BookingService(
    @Qualifier("gatewayWebClient") private val webClient: WebClient,
    private val bookingRepository: BookingRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun createBooking(
        request: CreateBookingRequest,
        authHeader: String,
        correlationId: String
    ): Mono<BookingDto> {

        val username = jwtTokenProvider.getUsernameFromToken(authHeader.replace("Bearer ", ""))

        return getUserId(username.toString(), authHeader, correlationId)
            .flatMap { userId ->
                if (request.autoSelect) {
                    selectRoomAutomatically(request.startDate, request.endDate, authHeader, correlationId)
                } else {
                    confirmRoom(request.roomId!!, request.startDate, request.endDate, authHeader, correlationId)
                }.map { roomId -> userId to roomId }
            }
            .flatMap { (userId, roomId) ->
                val booking = Booking(
                    userId = userId,
                    roomId = roomId,
                    startDate = request.startDate,
                    endDate = request.endDate
                )
                bookingRepository.save(booking)
                    .map { it.toDto() }
            }
    }

    @Transactional
    fun getBooking(id: Long, authHeader: String): Mono<BookingDto> {
        return getUserIdFromToken(authHeader)
            .flatMap { userId ->
                bookingRepository.findById(id)
                    .switchIfEmpty(Mono.error(NotFoundException("Booking not found: $id")))
                    .flatMap { booking ->
                        if (booking.userId != userId) {
                            Mono.error(RuntimeException("Not your booking"))
                        } else {
                            enrichBookingWithHotelAndRoom(booking, authHeader)
                        }
                    }
            }
    }

    private fun selectRoomAutomatically(
        start: LocalDate, end: LocalDate, auth: String, cid: String
    ): Mono<Long> {
        return webClient.get()
            .uri("/api/rooms/recommend?startDate=$start&endDate=$end")
            .header("Authorization", auth)
            .header("X-Correlation-Id", cid)
            .retrieve()
            .bodyToFlux(RoomDto::class.java)
            .next()
            .switchIfEmpty(Mono.error(IllegalStateException("No recommended rooms")))
            .map { it.id!! }
    }

    private fun confirmRoom(
        roomId: Long, start: LocalDate, end: LocalDate, auth: String, cid: String
    ): Mono<Long> {
        val availabilityRequest = AvailabilityRequest(UUID.randomUUID().toString(), start, end)
        return webClient.post()
            .uri("/api/rooms/$roomId/confirm-availability")
            .header("Authorization", auth)
            .header("X-Correlation-Id", cid)
            .bodyValue(availabilityRequest)
            .retrieve()
            .bodyToMono(Long::class.java)
    }

    private fun getUserId(username: String, auth: String, cid: String): Mono<Long> {
        return webClient.get()
            .uri("/api/user/$username")
            .header("Authorization", auth)
            .header("X-Correlation-Id", cid)
            .retrieve()
            .bodyToMono(UserDto::class.java)
            .map { it.id!! }
    }

    private fun getUserIdFromToken(authHeader: String): Mono<Long> {
        val token = jwtTokenProvider.extractToken(authHeader)
            ?: return Mono.error(IllegalArgumentException("Missing token"))

        return jwtTokenProvider.getUsernameFromToken(token)
            .flatMap { username ->
                webClient.get()
                    .uri("/api/user/$username")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(UserDto::class.java)
                    .map { it.id!! }
            }
    }

    private fun enrichBookingWithHotelAndRoom(
        booking: Booking,
        authHeader: String
    ): Mono<BookingDto> {
        return getRoom(booking.roomId, authHeader)
            .flatMap { room ->
                getHotel(room.hotelId, authHeader)
                    .map {
                        BookingDto(
                            id = booking.id,
                            userId = booking.userId,
                            roomId = booking.roomId,
                            startDate = booking.startDate,
                            endDate = booking.endDate,
                            status = booking.status,
                            createdAt = booking.createdAt
                        )
                    }
            }
    }

    // DELETE /booking/{id}
    @Transactional
    fun cancelBooking(id: Long, authHeader: String): Mono<Void> {
        return getUserIdFromToken(authHeader)
            .flatMap { userId ->
                bookingRepository.findById(id)
                    .switchIfEmpty(Mono.error(NotFoundException("Booking not found")))
                    .flatMap { booking ->
                        if (booking.userId != userId) {
                            Mono.error(RuntimeException("Not your booking"))
                        } else {
                            // Освобождаем комнату в Hotel service
                            releaseRoom(booking.roomId, authHeader)
                                .then(bookingRepository.save(booking.copy(status = "CANCELLED")))
                                .then()
                        }
                    }
            }
    }

    // GET /bookings
    @Transactional
    fun getUserBookings(authHeader: String): Flux<BookingDto> {
        return getUserIdFromToken(authHeader)
            .flatMapMany { userId ->
                bookingRepository.findByUserId(userId)
                    .flatMap { booking -> enrichBookingWithHotelAndRoom(booking, authHeader) }
            }
    }

    private fun getRoom(roomId: Long, authHeader: String): Mono<RoomDto> {
        return webClient.get()
            .uri("/api/rooms/$roomId")
            .header("Authorization", authHeader)
            .retrieve()
            .bodyToMono(RoomDto::class.java)
            .onErrorResume { Mono.error(RuntimeException("Room not found: $roomId")) }
    }

    private fun getHotel(hotelId: Long, authHeader: String): Mono<HotelDto> {
        return webClient.get()
            .uri("/api/hotels/$hotelId")
            .header("Authorization", authHeader)
            .retrieve()
            .bodyToMono(HotelDto::class.java)
            .onErrorResume { Mono.error(RuntimeException("Hotel not found: $hotelId")) }
    }

    private fun releaseRoom(roomId: Long, authHeader: String): Mono<Void> {
        return webClient.post()
            .uri("/api/rooms/$roomId/release-availability")
            .header("Authorization", authHeader)
            .retrieve()
            .bodyToMono(Void::class.java)
            .onErrorResume { Mono.empty() }
    }
}