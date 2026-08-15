package com.example.smsparser.parser.extractor

import com.example.smsparser.model.TransactionType

class TransactionTypeExtractor {

    fun extract(sms: String): TransactionType {

        val text = sms
            .lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()

        return when {

            // Refund/reversal must be checked first.
            text.contains("refund") ||
                    text.contains("refunded") ||
                    text.contains("reversal") -> {
                TransactionType.REFUND
            }

            text.contains("credited") ||
                    text.contains("received") -> {
                TransactionType.CREDIT
            }

            else -> {
                TransactionType.DEBIT
            }
        }
    }
}