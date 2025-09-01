package service

import com.arnav.UsageEvent
import com.arnav.service.MeterService
import org.jetbrains.exposed.sql.exposedLogger
import java.time.Instant
import java.time.temporal.ChronoUnit

class UsageEventProcessor(private val meterService: MeterService) {

    suspend fun processEvent(event: UsageEvent) {
        // Step 2: Query the Meters Table
        val cdfService = event.source
        val eventType = event.type

        val meterDao = meterService.findMeterByCdfServiceAndEventType(cdfService, eventType)

        // Step 3: Trim the time
        val time: Instant = event.time
        val windowSize = meterDao.windowSize.uppercase()

        val aggregatedTime = when (windowSize) {
            "MINUTE" -> time.truncatedTo(ChronoUnit.MINUTES)
            "HOUR" -> time.truncatedTo(ChronoUnit.HOURS)
            "DAY" -> time.truncatedTo(ChronoUnit.DAYS)
            else -> time
        }

        // For now, log the data. We'll add database and data warehouse steps later.
        exposedLogger.info("Processing $eventType & CDF Service $cdfService, Obtained Meter $meterDao")
        exposedLogger.info("Aggregated Time $aggregatedTime")

        // Step 4 & 5: Database and Data Warehouse logic will go here


        // We'll implement this in the next steps.
    }
}