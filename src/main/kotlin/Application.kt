package com.arnav

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.exposedLogger

fun main(args: Array<String>) {
    exposedLogger.info("Started Application")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabasesV2()
    configureRouting()
}
