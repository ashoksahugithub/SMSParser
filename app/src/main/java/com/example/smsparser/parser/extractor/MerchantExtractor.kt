package com.example.smsparser.parser.extractor

class MerchantExtractor {

    fun extract(sms: String): String? {

        val text = sms
            .replace(Regex("\\s+"), " ")
            .trim()

        /*
         * 1. "at MERCHANT with your ... Card"
         *
         * Example:
         *
         * at Blackwater Coffee, Gurgaon with your BOBCARD One
         * Credit Card ending in XX9907
         *
         * Result:
         * Blackwater Coffee, Gurgaon
         */
        val atWithCardPattern = Regex(
            """\bat\s+(.+?)\s+(?=with\s+(?:your\s+)?[\w\s]+?\b(?:credit\s+card|debit\s+card|card)\b)""",
            RegexOption.IGNORE_CASE
        )

        /*
         * 2. "to MERCHANT on your ... Card"
         *
         * Example:
         *
         * to HOSPITALITY PVT DELHI IN on your Edge Federal Bank
         * Credit Card ending 4422
         *
         * Result:
         * HOSPITALITY PVT DELHI IN
         */
        val toCardPattern = Regex(
            """\bto\s+(.+?)\s+(?=on\s+(?:your\s+)?[\w\s]+?\b(?:credit\s+card|debit\s+card|card)\b)""",
            RegexOption.IGNORE_CASE
        )

        /*
         * 3. Refund:
         *
         * "from BIGBASKET on 12-04-26"
         *
         * Result:
         * BIGBASKET
         *
         * This is deliberately generic.
         */
        val fromDatePattern = Regex(
            """\bfrom\s+(.+?)\s+(?=on\s+\d{1,2}[-/]\d{1,2}[-/]\d{2,4}\b)""",
            RegexOption.IGNORE_CASE
        )

        /*
         * 4. "at MERCHANT on DATE"
         *
         * Example:
         *
         * at AMAZON on 07-04-26
         *
         * Result:
         * AMAZON
         */
        val atDatePattern = Regex(
            """\bat\s+(.+?)\s+(?=on\s+\d{1,2}[-/]\d{1,2}[-/]\d{2,4}\b)""",
            RegexOption.IGNORE_CASE
        )

        /*
         * 5. "at MERCHANT. Available..."
         */
        val atAvailablePattern = Regex(
            """\bat\s+(.+?)(?=\.\s*(?:available|avl)\b)""",
            RegexOption.IGNORE_CASE
        )

        /*
         * 6. Generic "at MERCHANT" fallback.
         *
         * Stop before:
         * - on
         * - with
         * - sentence boundary
         */
        val atFallbackPattern = Regex(
            """\bat\s+(.+?)(?=\s+on\s+|\s+with\s+|[.!?]|$)""",
            RegexOption.IGNORE_CASE
        )

        val patterns = listOf(
            atWithCardPattern,
            toCardPattern,
            fromDatePattern,
            atDatePattern,
            atAvailablePattern,
            atFallbackPattern
        )

        for (pattern in patterns) {

            val match = pattern.find(text) ?: continue

            val merchant = cleanMerchant(
                match.groupValues[1]
            )

            if (merchant != null) {
                return merchant
            }
        }

        return null
    }

    private fun cleanMerchant(raw: String): String? {

        val merchant = raw
            .trim()
            .trim('.', ',', ':', ';')
            .replace(Regex("\\s+"), " ")

        if (merchant.isBlank()) {
            return null
        }

        val lower = merchant.lowercase()

        /*
         * Reject values that clearly aren't merchants.
         */
        val invalidFragments = listOf(
            "credit card",
            "debit card",
            "available limit",
            "available balance",
            "avl limit",
            "avl bal",
            "your account",
            "your a/c"
        )

        if (invalidFragments.any { lower.contains(it) }) {
            return null
        }

        return merchant
    }
}