package com.flycode

import com.flycode.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val port = System.getenv("PORT").toIntOrNull()
    embeddedServer(Netty, port = port ?: 8080, host = "0.0.0.0") {
                LanguageToolService.init()
                configureMonitoring()
                configureSecurity()
                configureSerialization()
                configureRouting()
            }
            .start(wait = true)
}
