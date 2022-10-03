package com.flycode.plugins

import com.flycode.LanguageToolService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class BulkCheckRequest(val texts: List<String>)

fun Application.configureRouting() {

    routing {
        post("/bulk-check") {
            val bulkCheckRequest = call.receive<BulkCheckRequest>()

            call.respond(
                LanguageToolService.check(bulkCheckRequest.texts)
            )
        }
        get("/") {
            call.respondText("Hello World")
        }
    }
}
