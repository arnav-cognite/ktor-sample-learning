package com.arnav.service

import com.arnav.model.MeterDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

class MeterService() {

    suspend fun findMeterByCdfServiceAndEventType(service: String, eventType: String): MeterDao =
        withContext(Dispatchers.IO) {
            val SELECT_METER_BY_CDF_SERVICE_AND_EVENT_TYPE =
                "SELECT metric_key, window_size, value_property, aggregation FROM meters WHERE cdf_service = '@1' AND event_type = '@2'"
            transaction {
                val result = exec(
                    SELECT_METER_BY_CDF_SERVICE_AND_EVENT_TYPE
                        .replace("@1", service.replace(Regex("[^a-zA-Z0-9 ]"), ""))
                        .replace("@2", eventType.replace(Regex("[^a-zA-Z0-9 ]"), ""))
                ) { resultSet ->
                    if (resultSet.next()) {
                        MeterDao(
                            cdfService = service,
                            eventType = eventType,
                            windowSize = resultSet.getString("window_size"),
                            metricKey = resultSet.getString("metric_key"),
                            valueProperty = resultSet.getString("value_property"),
                            aggregation = resultSet.getString("aggregation")
                        )
                    } else {
                        null
                    }
                }
                result ?: throw Exception("No Meter found with CDF service $service and EventType $eventType")
            }
        }
}