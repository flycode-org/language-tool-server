package com.flycode.languagetoolserver.plugins

import com.flycode.languagetoolserver.LanguageToolService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable data class BulkCheckRequest(val texts: List<String>)

@Serializable data class CheckRequest(val text: String)

fun Application.configureRouting() {

    routing {
        post("/check") {
            val bulkCheckRequest = call.receive<CheckRequest>()

            call.respond(LanguageToolService.check(bulkCheckRequest.text))
        }
        post("/bulk-check") {
            val bulkCheckRequest = call.receive<BulkCheckRequest>()

            call.respond(LanguageToolService.check(bulkCheckRequest.texts))
        }
        get("/") { call.respondText("Hello World!") }
    }
}
