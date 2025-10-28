package ru.dan.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class BookingApplication

fun main(args: Array<String>) {
	runApplication<BookingApplication>(*args)
}
