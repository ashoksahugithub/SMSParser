package com.example.smsparser.model

import java.time.LocalDateTime

data class Transaction(
    val amount: Double,
    val currency: String,
    val bankName: String,
    val cardLastFour: String?,
    val merchantName: String?,
    val date: String?,
    val type: TransactionType
)
