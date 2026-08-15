package com.example.smsparser.parser.extractor

class MerchantExtractor {

    private val patterns = listOf(

        Regex(
            """(?i)\bat\s+(.+?)\s+(?:on|at)\s+\d{1,2}[/-]"""
        ),

        Regex(
            """(?i)\bat\s+(.+?)\s+on\s+\d{1,2}[/-]"""
        ),

        Regex(
            """(?i)\bto\s+([A-Za-z0-9][A-Za-z0-9 .&_-]{2,40})\s+on\s+\d{1,2}[/-]"""
        ),

        Regex(
            """(?i)\bfrom\s+([A-Za-z0-9][A-Za-z0-9 .&_-]{2,40})\s+on\s+\d{1,2}[/-]"""
        ),

        Regex(
            """(?i)\bby\s+([A-Za-z0-9][A-Za-z0-9 .&_-]{2,40})\.\s+Avl"""
        )
    )

    fun extract(sms: String): String? {

        for (pattern in patterns) {

            val match = pattern.find(sms)

            if (match != null) {
                return clean(match.groupValues[1])
            }
        }

        return null
    }

    private fun clean(value: String): String {

        return value
            .trim()
            .trim('.', ',', ':', ';')
    }
}