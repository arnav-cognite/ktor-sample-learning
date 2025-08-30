package com.arnav

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import java.lang.Exception
import java.sql.Connection
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        // Serialize OffsetDateTime to an ISO 8601 string
        encoder.encodeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        // Deserialize from an ISO 8601 string back to OffsetDateTime
        return OffsetDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}

@Serializable
data class Meter(
    val meterId: Long,
    val externalId: String,
    val quotaExternalId: String?,
    val cdfService: String,
    val metricKey: String,
    val description: String?,
    val eventType: String,
    val valueProperty: String?,
    val aggregation: String,
    @Contextual val groupBy: Map<String, JsonElement>?,
    val windowSize: String = "MINUTE",
    // @Serializable(with = OffsetDateTimeSerializer::class) explicitly tells the serialization library
    // to use the custom serializer defined above for OffsetDateTime.
    @Serializable(with = OffsetDateTimeSerializer::class) val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = OffsetDateTimeSerializer::class) val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
data class MeterDao(
    val cdfService: String,
    val metricKey: String,
    val eventType: String,
    val valueProperty: String?,
    val aggregation: String,
    val windowSize: String = "MINUTE",
    // @Serializable(with = OffsetDateTimeSerializer::class) explicitly tells the serialization library
    // to use the custom serializer defined above for OffsetDateTime.
    @Serializable(with = OffsetDateTimeSerializer::class) val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = OffsetDateTimeSerializer::class) val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
class MeterService(private val connection: Connection) {
    companion object {
        private const val SELECT_METER_BY_CDF_SERVICE_AND_EVENT_TYPE = "SELECT metric_key, window_size, value_property, aggregation FROM meters WHERE cdf_service = ? AND event_type = ?"
    }
    init {
        val statement = connection.createStatement()
    }

    suspend fun findMeterByCdfServiceAndEventType(service: String, eventType: String): MeterDao = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_METER_BY_CDF_SERVICE_AND_EVENT_TYPE)
        statement.setString(1, service)
        statement.setString(2, eventType)

        val rs = statement.executeQuery()
        if(rs.next()){
            val metricKey = rs.getString("metric_key")
            val windowSize = rs.getString("window_size")
            return@withContext MeterDao(cdfService = service, eventType = eventType, windowSize = windowSize,
                metricKey = metricKey, valueProperty = rs.getString("value_property"),
                aggregation = rs.getString("aggregation"))
        }
        else{
            throw Exception("No Meter found with CDF service $service and EventType $eventType")
        }
    }
}