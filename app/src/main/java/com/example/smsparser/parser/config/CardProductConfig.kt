package com.example.smsparser.parser.config

/**
 * Represents a card/product branding that can identify
 * the underlying issuer bank.
 *
 * Example:
 *
 * Edge Federal Bank Credit Card
 * -> Federal Bank
 *
 * BOBCARD One Credit Card
 * -> Bank of Baroda
 */
data class CardProductConfig(
    val productName: String,
    val issuerBank: String,
    val patterns: List<String>
)