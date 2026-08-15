package com.example.smsparser.parser.extractor

class AmountExtractor {

    private val amountRegex = Regex(
        pattern = """(?i)\b(?:Rs\.?|INR|USD|EUR|AED)\s*([\d,]+(?:\.\d{1,2})?)"""
    )

    fun extract(sms: String): Double? {
        val match = amountRegex.find(sms) ?: return null

        return match.groupValues
            .getOrNull(1)
            ?.replace(",", "")
            ?.toDoubleOrNull()
    }
}