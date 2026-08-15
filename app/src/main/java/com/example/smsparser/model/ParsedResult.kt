package com.example.smsparser.model

data class ParsedResult(
    val id: Int,
    val rawSms: String,
    val decision: Decision,
    val excludeReason: ExcludeReason?,
    val transaction: Transaction?,
    val confidence: Double
)