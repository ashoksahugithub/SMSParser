package com.example.smsparser.parser.config

data class BankConfig(
    val canonicalName: String,
    val aliases: List<String>
)

object BankConfigs {

    val banks = listOf(

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
            canonicalName = "Federal Bank",
            aliases = listOf(
                "federal bank",
                "federal",
                "edge federal bank"
            )
        ),

        BankConfig(
            canonicalName = "Bank of Baroda",
            aliases = listOf(
                "bobcard",
                "bob card",
                "bobcard one"
            )
        )
    )
}