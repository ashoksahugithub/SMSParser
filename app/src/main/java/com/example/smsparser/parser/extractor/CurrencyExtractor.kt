package com.example.smsparser.parser.extractor

class CurrencyExtractor {

    private val currencyRegex = Regex(
        pattern = """(?i)\b(INR|USD|EUR|AED)\b|(?:Rs\.?)"""
    )

    fun extract(sms: String): String {
        val match = currencyRegex.find(sms) ?: return "INR"

        return when {
            match.groupValues.any { it.equals("USD", true) } -> "USD"
            match.groupValues.any { it.equals("EUR", true) } -> "EUR"
            match.groupValues.any { it.equals("AED", true) } -> "AED"
            else -> "INR"
        }
    }
}