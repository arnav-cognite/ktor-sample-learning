package com.arnav

import com.arnav.service.MeterService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import service.UsageEventProcessor

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    val meterService = MeterService()
    val eventProcessor = UsageEventProcessor(meterService)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        get("/health") {
            // Execute the database check on a separate thread to avoid blocking the main event loop
            val isConnected = withContext(Dispatchers.IO) {
                isDbConnectedV2()
            }

            if (isConnected) {
                call.respond(HttpStatusCode.OK, "Database connection is healthy.")
            } else {
                call.respond(HttpStatusCode.ServiceUnavailable, "Database connection is unavailable.")
            }
        }

        // New endpoint to accept Usage Events
        post("/api/v1/ingest/usage-event") {
            try {

                val usageEvent = call.receive<UsageEvent>()
                log.info("Received event: $usageEvent")

                // Call the new processor to handle the event
                eventProcessor.processEvent(usageEvent)

                call.respond(HttpStatusCode.Accepted, mapOf("status" to "Event accepted for processing."))

            } catch (e: Exception) {
                // Respond with a Bad Request status if deserialization fails
                call.respond(HttpStatusCode.BadRequest, "Failed to parse JSON: ${e.message}")
            }
        }
    }
}