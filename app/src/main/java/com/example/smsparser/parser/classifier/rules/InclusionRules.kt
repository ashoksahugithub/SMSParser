package com.example.smsparser.parser.classifier.rules

import com.example.smsparser.parser.config.ParserConfig

object InclusionRules {

    fun isRelevantCreditCardTransaction(text: String): Boolean {

        val hasCreditCardIdentity =
            hasCreditCardIdentity(text)

        if (!hasCreditCardIdentity) {
            return false
        }

        val hasCompletedTransaction =
            hasCompletedTransaction(text)

        if (!hasCompletedTransaction) {
            return false
        }

        if (isFutureTransaction(text)) {
            return false
        }

        return true
    }

    private fun hasCreditCardIdentity(text: String): Boolean {

        val patterns =
            ParserConfig.cardProductConfig.creditCardPatterns

        return patterns.any { pattern ->
            text.contains(pattern)
        }
    }

    private fun hasCompletedTransaction(text: String): Boolean {

        return text.contains("spent") ||
                text.contains("charged") ||
                text.contains("purchase") ||
                text.contains("purchased") ||
                text.contains("transaction") ||
                text.contains("refund")
    }

    private fun isFutureTransaction(text: String): Boolean {

        return text.contains("will be debited") ||
                text.contains("will be auto debited") ||
                text.contains("scheduled") ||
                text.contains("e-mandate")
    }
}