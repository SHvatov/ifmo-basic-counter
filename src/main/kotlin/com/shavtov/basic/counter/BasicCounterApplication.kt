package com.shavtov.basic.counter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
class BasicCounterApplication

@RestController
class BaseController {

    private val counter = AtomicLong(0L)

    @GetMapping("/")
    fun get(): Long = counter.get()

    @GetMapping("/stat")
    fun getAndIncrement(): Long = counter.getAndIncrement()

    @GetMapping(value = ["/about"], produces = [MediaType.TEXT_HTML_VALUE])
    fun about(rq: HttpServletRequest): String =
        rq.requestURL
            .toString()
            .replace(rq.requestURI, "")
            .let {
                """
                        <html>
                            <body>
                                <h3>Hello!</h3>
                                <b>Hostname:</b>${it}<br/>
                            </body>
                        </html>
                    """.trimIndent()
            }
}

fun main(args: Array<String>) {
    runApplication<BasicCounterApplication>(*args)
}
