package com.example.smsparser.parser.extractor

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateExtractor {

    private val dateRegex = Regex(
        """\b\d{1,2}[/-](?:\d{1,2}|[A-Za-z]{3})[/-]\d{2,4}\b"""
    )

    private val formatters = listOf(
        DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
    )

    fun extract(sms: String): String? {

        val match = dateRegex.find(sms) ?: return null

        val value = match.value.replace("/", "-")

        for (formatter in formatters) {
            try {
                return LocalDate
                    .parse(value, formatter)
                    .toString()
            } catch (_: Exception) {
                // Try next format.
            }
        }

        return null
    }
}