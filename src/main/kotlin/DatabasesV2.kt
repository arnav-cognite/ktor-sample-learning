package com.arnav

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime

fun Application.configureDatabasesV2() {
    val config = HikariConfig().apply {
        jdbcUrl = environment.config.property("postgres.url").getString()
        username = environment.config.property("postgres.user").getString()
        password = environment.config.property("postgres.password").getString()
        driverClassName = "org.postgresql.Driver"
        isAutoCommit = false // Ensures transactions are handled correctly
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        maximumPoolSize = 10 // Example: you can configure the max number of connections
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    log.info("Connected to Postgres database using a connection pool.")
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