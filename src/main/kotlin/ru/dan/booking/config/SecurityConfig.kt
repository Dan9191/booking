package ru.dan.booking.config

import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono
import javax.crypto.SecretKey

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    private val secret: SecretKey = Keys.hmacShaKeyFor(
        "your-256-bit-secret-key-1234567890abcdef".toByteArray()
    )

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange {
                it
                    .pathMatchers("/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**").permitAll()
                    .pathMatchers("/actuator/**", "/api/booking/hi").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/booking", "/api/booking/*").hasAnyAuthority("ROLE_USER", "ROLE_BOOKING_SERVICE", "ROLE_ADMIN")
                    .pathMatchers(HttpMethod.POST, "/api/booking/*").hasAnyAuthority("ROLE_USER", "ROLE_BOOKING_SERVICE", "ROLE_ADMIN")
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth ->
                oauth.jwt { jwt ->
                    jwt.jwtDecoder(jwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .build()
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder.withSecretKey(secret)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, out Mono<out AbstractAuthenticationToken>> {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
            setAuthorityPrefix("ROLE_")
            setAuthoritiesClaimName("roles")
        }

        val jwtAuthenticationConverter = JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        }

        return ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
    }
}