package com.example.smsparser.parser.extractor

class CardExtractor {

    private val cardPatterns = listOf(
        Regex(
            """(?i)(?:credit\s+card|card)\s+(?:no\.?\s*)?(?:xx|\*+)?(\d{4})\b"""
        ),
        Regex(
            """(?i)(?:credit\s+card).*?(?:ending\s+(?:in\s+)?(?:xx|\*+)?)(\d{4})\b"""
        ),
        Regex(
            """(?i)(?:credit\s+card).*?xx(\d{4})\b"""
        )
    )

    fun extractLastFour(sms: String): String? {

        for (pattern in cardPatterns) {
            val match = pattern.find(sms)

            if (match != null) {
                return match.groupValues[1]
            }
        }

        return null
    }
}