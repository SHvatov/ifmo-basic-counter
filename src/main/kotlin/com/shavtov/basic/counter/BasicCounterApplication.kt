package com.shavtov.basic.counter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
class BasicCounterApplication

@Configuration
class BasicCounterConfiguration {

    @Bean
    fun redisCounter(factory: RedisConnectionFactory) =
        RedisAtomicLong(BACKING_COUNTER_NAME, factory)

    private companion object {
        const val BACKING_COUNTER_NAME = "ifmo-counter"
    }
}

@RestController
class BaseController {

    @set:Autowired
    lateinit var redisCounter: RedisAtomicLong

    @GetMapping("/")
    fun get() = redisCounter.get()

    @GetMapping("/stat")
    fun getAndIncrement() = redisCounter.andIncrement

    @GetMapping(path = ["/about"], produces = [MediaType.TEXT_HTML_VALUE])
    fun about(rq: HttpServletRequest): String =
        rq.requestURL
            .toString()
            .replace(rq.requestURI, "")
            .let {
                """
                        <html>
                            <body>
                                <h3>Hello, Sergey Khvatov!</h3>
                                <b>Hostname:</b>${it}<br/>
                            </body>
                        </html>
                    """.trimIndent()
            }
}

fun main(args: Array<String>) {
    runApplication<BasicCounterApplication>(*args)
}
