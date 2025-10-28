package ru.dan.booking.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import reactor.core.publisher.Mono
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration:3600000}") private val jwtExpiration: Long
) {

    private val key: Key by lazy { Keys.hmacShaKeyFor(jwtSecret.toByteArray()) }

    /**
     * Извлекает username из токена
     */
    fun getUsernameFromToken(token: String): Mono<String> {
        return Mono.fromCallable {
            getClaims(token).subject
        }.onErrorResume { Mono.empty() }
    }

    /**
     * Получить все claims
     */
    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    /**
     * Валидация токена (подпись + срок)
     */
    fun validateToken(token: String): Mono<Boolean> {
        return Mono.fromCallable {
            val claims = getClaims(token)
            val expiration = claims.expiration
            expiration != null && expiration.after(Date())
        }.onErrorResume { Mono.just(false) }
    }

    /**
     * Извлечь роли (например, "ROLE_USER,ROLE_ADMIN")
     */
    fun getRolesFromToken(token: String): Mono<List<String>> {
        return Mono.fromCallable {
            val roles = getClaims(token).get("roles", String::class.java)
            roles?.split(",")?.map { it.trim() } ?: emptyList()
        }.onErrorResume { Mono.empty() }
    }

    /**
     * Извлечь токен из заголовка "Authorization: Bearer <token>"
     */
    fun extractToken(authHeader: String?): String? {
        return authHeader?.takeIf { it.startsWith("Bearer ") }?.substring(7)
    }
}