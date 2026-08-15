package com.example.smsparser.parser.extractor

class BankResolver {

    private val bankAliases = linkedMapOf(

        "HDFC Bank" to listOf(
            "hdfc bank",
            "hdfc"
        ),

        "ICICI Bank" to listOf(
            "icici bank",
            "icici"
        ),

        "Axis Bank" to listOf(
            "axis bank",
            "axis"
        ),

        "YES BANK" to listOf(
            "yes bank",
            "yesbank"
        ),

        "Federal Bank" to listOf(
            "federal bank"
        ),

        "Bank of Baroda" to listOf(
            "bank of baroda",
            "bobcard",
            "bob card"
        ),

        "SBI" to listOf(
            "state bank of india",
            "sbi"
        ),

        "Kotak Mahindra Bank" to listOf(
            "kotak mahindra bank",
            "kotak bank",
            "kotak"
        )
    )

    fun resolve(sms: String): String? {

        val text = sms
            .lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()

        for ((canonicalName, aliases) in bankAliases) {

            for (alias in aliases) {

                val pattern = Regex(
                    """(?<![a-z])${Regex.escape(alias)}(?![a-z])""",
                    RegexOption.IGNORE_CASE
                )

                if (pattern.containsMatchIn(text)) {
                    return canonicalName
                }
            }
        }

        return null
    }
}