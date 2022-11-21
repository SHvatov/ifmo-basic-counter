package com.shavtov.basic.counter

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
internal class BasicCounterApplicationTest {

    @Test
    fun createContext() = Unit

    private companion object {

        @JvmStatic
        @Suppress("unused")
        val REDIS: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .apply {
                    start()
                    System.setProperty("spring.redis.host", host);
                    System.setProperty("spring.redis.port", getMappedPort(6379).toString());
                };
    }
}