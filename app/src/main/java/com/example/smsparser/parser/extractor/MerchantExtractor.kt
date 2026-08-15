package com.example.smsparser.parser.extractor

class MerchantExtractor {

    fun extract(sms: String): String? {

        val patterns = listOf(

            // Example:
            // spent Rs 1836.00 to HOSPITALITY PVT DELHI IN on your Edge Federal Bank Credit Card
            Regex(
                """spent\s+(?:rs\.?|inr)\s*[\d,]+(?:\.\d+)?\s+to\s+(.+?)\s+on\s+(?:your\s+)?""",
                RegexOption.IGNORE_CASE
            ),

            // Example:
            // spent Rs. 849.00 at Blackwater Coffee, Gurgaon with your BOBCARD
            Regex(
                """spent\s+(?:rs\.?|inr)\s*[\d,]+(?:\.\d+)?\s+at\s+(.+?)\s+with\s+(?:your\s+)?""",
                RegexOption.IGNORE_CASE
            ),

            // Example:
            // Refund of Rs 450.00 has been credited ... from BIGBASKET on ...
            Regex(
                """refund\s+of\s+(?:rs\.?|inr)\s*[\d,]+(?:\.\d+)?\s+has\s+been\s+credited.*?\s+from\s+(.+?)\s+on\s+""",
                RegexOption.IGNORE_CASE
            )
        )

        for (pattern in patterns) {
            val match = pattern.find(sms)

            if (match != null) {
                return match.groupValues[1]
                    .trim()
                    .trimEnd('.', ',')
            }
        }

        return null
    }
}