package com.example.smsparser.parser.config

/**
 * Central configuration used by the parser.
 *
 * Adding a new bank/card product should primarily require
 * adding configuration here rather than modifying parser logic.
 */
data class ParserConfig(
    val banks: List<BankConfig>,
    val cardProducts: List<CardProductConfig>
) {

    companion object {

        val DEFAULT = ParserConfig(

            banks = listOf(

                BankConfig(
                    canonicalName = "HDFC Bank",
                    aliases = listOf(
                        "hdfc bank",
                        "hdfc"
                    )
                ),

                BankConfig(
                    canonicalName = "ICICI Bank",
                    aliases = listOf(
                        "icici bank",
                        "icici"
                    )
                ),

                BankConfig(
                    canonicalName = "Axis Bank",
                    aliases = listOf(
                        "axis bank",
                        "axis"
                    )
                ),

                BankConfig(
                    canonicalName = "YES BANK",
                    aliases = listOf(
                        "yes bank",
                        "yesbank"
                    )
                ),

                BankConfig(
                    canonicalName = "Bank of Baroda",
                    aliases = listOf(
                        "bank of baroda",
                        "bob",
                        "bob bank"
                    )
                ),

                BankConfig(
                    canonicalName = "Federal Bank",
                    aliases = listOf(
                        "federal bank"
                    )
                ),

                BankConfig(
                    canonicalName = "SBI",
                    aliases = listOf(
                        "state bank of india",
                        "sbi"
                    )
                )

            ),

            cardProducts = listOf(

                CardProductConfig(
                    productName = "Edge Federal Bank",
                    issuerBank = "Federal Bank",
                    patterns = listOf(
                        "edge federal bank",
                        "edge federal bank credit card",
                        "edge federal"
                    )
                ),

                CardProductConfig(
                    productName = "BOBCARD One",
                    issuerBank = "Bank of Baroda",
                    patterns = listOf(
                        "bobcard one",
                        "bobcard one credit card",
                        "bobcard"
                    )
                )

            )
        )
    }
}