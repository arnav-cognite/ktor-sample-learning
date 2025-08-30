package com.arnav

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class UsageEvent(
    val type: String,
    val time: String,
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