package com.shavtov.basic.counter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
@EnableMongoRepositories
class BasicCounterApplication

@Configuration
class BasicCounterConfiguration {

    @Bean
    fun redisCounter(factory: RedisConnectionFactory) = RedisAtomicLong(BACKING_COUNTER_NAME, factory)

    private companion object {
        const val BACKING_COUNTER_NAME = "ifmo-counter"
    }
}

@Document
data class CounterModification(
    @Id val value: Long, val userInfo: String = "none", val modified: LocalDateTime = LocalDateTime.now()
)

@Repository
interface CounterModificationRepository : MongoRepository<CounterModification, Long>

@RestController
class BaseController {

    @set:Autowired
    lateinit var redisCounter: RedisAtomicLong

    @set:Autowired
    lateinit var counterModificationRepository: CounterModificationRepository

    @GetMapping("/")
    fun get() = redisCounter.get()

    @GetMapping("/all", produces = [APPLICATION_JSON_VALUE])
    fun getAll(): List<CounterModification> =
        counterModificationRepository.findAll()

    @GetMapping("/stat")
    fun getAndIncrement(rq: HttpServletRequest): Long {
        return redisCounter.andIncrement.also {
            counterModificationRepository.save(
                CounterModification(
                    value = it, userInfo = rq.getHeader("User-Agent") ?: "none"
                )
            )
        }
    }

    @GetMapping(path = ["/about"], produces = [MediaType.TEXT_HTML_VALUE])
    fun about(rq: HttpServletRequest): String = rq.requestURL.toString().replace(rq.requestURI, "").let {
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
