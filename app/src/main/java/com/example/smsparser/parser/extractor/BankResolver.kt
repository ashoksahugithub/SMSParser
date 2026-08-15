package com.example.smsparser.parser.extractor

class BankResolver {

    private data class BankRule(
        val bankName: String,
        val patterns: List<Regex>
    )

    private val rules = listOf(

        BankRule(
            bankName = "HDFC Bank",
            patterns = listOf(
                Regex("""(?i)\bHDFC\s+Bank\b"""),
                Regex("""(?i)\bHDFC\s+Credit\s+Card\b""")
            )
        ),

        BankRule(
            bankName = "ICICI Bank",
            patterns = listOf(
                Regex("""(?i)\bICICI\s+Bank\b"""),
                Regex("""(?i)\bICICI\s+Credit\s+Card\b""")
            )
        ),

        BankRule(
            bankName = "Axis Bank",
            patterns = listOf(
                Regex("""(?i)\bAxis\s+Bank\b"""),
                Regex("""(?i)\bAxis\s+Bank\s+Card\b""")
            )
        ),

        BankRule(
            bankName = "YES BANK",
            patterns = listOf(
                Regex("""(?i)\bYES\s+BANK\b""")
            )
        ),

        BankRule(
            bankName = "Federal Bank",
            patterns = listOf(
                Regex("""(?i)\bFederal\s+Bank\b"""),
                Regex("""(?i)\bEdge\s+Federal\s+Bank\b""")
            )
        ),

        BankRule(
            bankName = "Bank of Baroda",
            patterns = listOf(
                Regex("""(?i)\bBOBCARD\b"""),
                Regex("""(?i)\bBOBCARD\s+One\b""")
            )
        )
    )

    fun resolve(sms: String): String? {

        for (rule in rules) {
            if (rule.patterns.any { it.containsMatchIn(sms) }) {
                return rule.bankName
            }
        }

        return null
    }
}