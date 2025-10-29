package ru.dan.booking.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.security.Key

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}") private val jwtSecret: String, ) {

    /**
     * Извлекает username из токена
     */
    fun getUsernameFromToken(token: String): Mono<String> {
        return Mono.fromCallable {
            getClaims(token).subject
        }.onErrorResume { Mono.empty() }
    }

    fun getClaims(token: String): Claims {
        val keyBytes = jwtSecret.toByteArray(StandardCharsets.UTF_8)
        val key = Keys.hmacShaKeyFor(keyBytes)

        val jwt = Jwts.parser() // заменено на parserBuilder()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)

        return jwt.payload
    }

    /**
     * Извлечь токен из заголовка "Authorization: Bearer <token>"
     */
    fun extractToken(authHeader: String?): String? {
        return authHeader?.takeIf { it.startsWith("Bearer ") }?.substring(7)
    }
}