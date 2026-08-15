package com.example.smsparser.parser.config

data class CardProductConfig(
    val creditCardPatterns: List<String>,
    val debitCardPatterns: List<String>
)

object CardProductConfigs {

    val default = CardProductConfig(

        creditCardPatterns = listOf(
            "credit card",
            "creditcard",
            "credit card ending",
            "credit card xx",
            "credit card *",
            "card ending in"
        ),

        debitCardPatterns = listOf(
            "debit card",
            "debitcard",
            "debit card ending",
            "debit card xx",
            "debit card *"
        )
    )
}