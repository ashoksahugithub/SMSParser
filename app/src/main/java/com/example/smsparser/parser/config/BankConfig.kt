package com.example.smsparser.parser.config

/**
 * Represents an issuer bank.
 *
 * aliases:
 *  Different names by which the bank may appear in SMS text.
 */
data class BankConfig(
    val canonicalName: String,
    val aliases: List<String>
)