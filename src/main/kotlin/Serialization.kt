package com.arnav

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

fun Application.configureSerialization() {
    // Install the ContentNegotiation plugin
    // This allows KTor to automatically handle JSON serialization/deserialization
    install(ContentNegotiation) {
        json(Json {
            // Configure JSON behavior here if needed
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true // A good practice to avoid errors if the JSON schema changes
        })
    }
}
