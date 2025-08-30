package com.arnav

import java.sql.Connection

object ConnectionManager {
    var connection: Connection? = null
        private set

    fun setConnection(conn: Connection) {
        if (connection == null) {
            connection = conn
        } else {
            println("Warning: Database connection already set.")
        }
    }

    fun getOrThrowConnection(): Connection {
        return connection ?: throw IllegalStateException("Database connection not initialized.")
    }
}