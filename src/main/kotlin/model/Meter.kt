package com.arnav.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import util.InstantSerializer
import java.time.Instant

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
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant
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
    @Serializable(with = InstantSerializer::class) val createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant = Instant.now()
)