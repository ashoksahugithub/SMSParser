package com.example.smsparser.parser.confidence

class ConfidenceCalculator {

    fun calculate(
        amount: Double?,
        bank: String?,
        card: String?,
        merchant: String?,
        date: String?
    ): Double {

        var score = 0.0

        if (amount != null) score += 0.25
        if (bank != null) score += 0.20
        if (card != null) score += 0.20
        if (merchant != null) score += 0.20
        if (date != null) score += 0.15

        return score.coerceIn(0.0, 1.0)
    }
}