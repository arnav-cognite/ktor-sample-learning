package com.arnav

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class UsageEventProcessor(private val meterService: MeterService) {

    suspend fun processEvent(event: UsageEvent) {
        // Step 2: Query the Meters Table
        val cdfService = event.source
        val eventType = event.type

        val meterDao = meterService.findMeterByCdfServiceAndEventType(cdfService, eventType)

        // Step 3: Trim the time
        val time = OffsetDateTime.parse(event.time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val windowSize = meterDao.windowSize.uppercase()

        val aggregatedTime = when (windowSize) {
            "MINUTE" -> time.truncatedTo(ChronoUnit.MINUTES)
            "HOUR" -> time.truncatedTo(ChronoUnit.HOURS)
            "DAY" -> time.truncatedTo(ChronoUnit.DAYS)
            else -> time
        }

        // You can convert this back to a string or use it for the database insert.
        val aggregatedTimeString = aggregatedTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        // For now, log the data. We'll add database and data warehouse steps later.
        println("Processing event...")
        println("  Source: ${event.source}")
        println("  Event Type: ${event.type}")
        println("  Meter Info: ${meterDao}")
        println("  Original Time: ${event.time}")
        println("  Aggregated Time: $aggregatedTimeString")

        // Step 4 & 5: Database and Data Warehouse logic will go here
        // We'll implement this in the next steps.
    }
}