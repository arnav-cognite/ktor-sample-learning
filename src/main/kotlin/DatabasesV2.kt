package com.arnav

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.sql.DriverManager

fun Application.configureDatabasesV2() {
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    val dbConnection = DriverManager.getConnection(url, user, password)
    ConnectionManager.setConnection(dbConnection)

    Database.connect(
        url = url,
        user = user,
        driver = "org.postgresql.Driver",
        password = password
    )

    exposedLogger.info("Connected to Postgres database at $url")

}

fun isDbConnectedV2(): Boolean {
    return try {
        // Run a simple transaction to test the connection
        transaction {
            // A non-destructive query, like selecting the current time from the database
            val result = exec("SELECT cdf_service FROM service_plans where external_id='BASIC_TS_PLAN'; ") { rs ->
                rs.next()
                rs.getObject(1, String::class.java)
            }
            exposedLogger.info("Database health check successful: Dummy data fetched from service_plans table is $result")

            val resultTime = exec("SELECT now() ") { rs ->
                rs.next()
                rs.getObject(1, OffsetDateTime::class.java)
            }
            exposedLogger.info("Database health check successful: current time is $resultTime")
            true
        }
    } catch (e: Exception) {
        // Log the exception to see what went wrong
        exposedLogger.error("Database connection failed!", e)
        false
    }
}