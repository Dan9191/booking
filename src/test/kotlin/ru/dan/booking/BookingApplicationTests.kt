package ru.dan.booking

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import ru.dan.auth_service.config.BaseTestWithContext
import ru.dan.booking.models.CreateBookingRequest
import java.time.LocalDate
import java.util.*

class BookingControllerTest : BaseTestWithContext() {

    @Autowired
    lateinit var webTestClient: WebTestClient

//    @Test
//    fun `getUserBookings returns bookings`() {
//        val token = createJwt("john", "ROLE_USER")
//        val bookingId = createBooking(token)
//
//        webTestClient.get()
//            .uri("/api/booking")
//            .header("Authorization", "Bearer $token")
//            .header("X-Correlation-Id", "list-1")
//            .exchange()
//            .expectStatus().isOk
//            .expectBody<List<Map<String, Any>>>()
//            .consumeWith { response ->
//                val bookings = response.responseBody!!
//                assertEquals(1, bookings.size)
//
//                val booking = bookings[0]
//                assertEquals(bookingId, booking["id"])
//                assertEquals(42, booking["userId"])
//                assertEquals(101, booking["roomId"])
//                assertEquals("ACTIVE", booking["status"])
//            }
//    }
//
//    private fun createBooking(token: String): Long {
//        val request = CreateBookingRequest(
//            autoSelect = true,
//            startDate = LocalDate.of(2025, 11, 1),
//            endDate = LocalDate.of(2025, 11, 5)
//        )
//
//        return webTestClient.post()
//            .uri("/api/booking")
//            .header("Authorization", "Bearer $token")
//            .header("X-Correlation-Id", "helper")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(request)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody<Map<String, Any>>()
//            .returnResult()
//            .responseBody!!["id"] as Long
//    }
//
//    private fun createJwt(username: String, vararg roles: String): String {
//        val key = Keys.hmacShaKeyFor("your-256-bit-secret-key-1234567890abcdef".toByteArray())
//        return Jwts.builder()
//            .subject(username)
//            .claim("roles", roles.joinToString(","))
//            .issuedAt(Date())
//            .expiration(Date(System.currentTimeMillis() + 3600000))
//            .signWith(key)
//            .compact()
//    }
}