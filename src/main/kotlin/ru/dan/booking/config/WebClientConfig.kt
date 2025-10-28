package ru.dan.booking.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig(
    @Value("\${app.gateway.url}") private val gatewayUrl: String,
) {

    @Bean
    fun gatewayWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(gatewayUrl)
            .filter(ExchangeFilterFunction.ofRequestProcessor { clientRequest -> Mono.just(clientRequest).map { req -> req } })
            .build()
    }
}