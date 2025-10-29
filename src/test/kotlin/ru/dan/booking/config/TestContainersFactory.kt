package ru.dan.auth_service.config

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.Wait
import org.wiremock.integrations.testcontainers.WireMockContainer

/**
 * Синглтон фабрика тест контейнеров.
 */
object TestContainersFactory {

    private const val WIREMOCK_IMAGE = "wiremock/wiremock:2.33.2"


    val WIREMOCK_CONTAINER = WireMockContainer(WIREMOCK_IMAGE)
    .withClasspathResourceMapping("wiremock", "/home/wiremock", BindMode.READ_ONLY)
    .waitingFor(Wait.forHttp("/__admin").forStatusCode(200));

    init {
        WIREMOCK_CONTAINER.start();


        Runtime.getRuntime().addShutdownHook(Thread {
            WIREMOCK_CONTAINER.stop();
        })
    }
}