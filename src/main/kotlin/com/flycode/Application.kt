package com.flycode

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.flycode.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureMonitoring()
        configureSecurity()
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
