package com.arnav

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import util.InstantSerializer
import java.time.Instant

@Serializable
data class UsageEvent(
    val type: String,
    @Serializable(with = InstantSerializer::class) val time: Instant,
    val id: String,
    val source: String,
    val project: String,
    val data: UsageData,
    val metadata: UsageMetadata? = null // Optional field
)

@Serializable
data class UsageData(
    @Contextual val cognite_coin_count: Double?
)

@Serializable
data class UsageMetadata(
    val endpoint: String? = null,
    val agent_id: String? = null,
    val llm_model: String? = null,
    val input_token_count: Int? = null,
    val output_token_count: Int? = null
)