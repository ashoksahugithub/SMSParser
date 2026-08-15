package com.example.smsparser.parser.classifier

import com.example.smsparser.model.ExcludeReason
import com.example.smsparser.parser.classifier.rules.ExclusionRules
import com.example.smsparser.parser.classifier.rules.InclusionRules

class SmsClassifier {

    fun classify(sms: String): ClassificationResult {

        val text = normalize(sms)

        // 1. Invalid / empty SMS
        if (text.isBlank()) {
            return ClassificationResult.Exclude(
                reason = ExcludeReason.MALFORMED_SMS,
                confidence = 1.0
            )
        }

        /*
         * 2. Explicit card refund.
         *
         * A refund is included only when it is credited/refunded
         * to a card.
         *
         * Examples included:
         *
         * "Refund ... credited to your HDFC Card xx5678
         *  from BIGBASKET..."
         *
         * "Refund ... to your Credit Card..."
         *
         * Bank-account / UPI refunds are NOT matched here.
         */
        if (InclusionRules.isCardRefund(text)) {
            return ClassificationResult.Include
        }

        /*
         * 3. Exclusion rules have priority for all other messages.
         *
         * This handles:
         * OTP
         * UPI bank-account transactions
         * savings-account transactions
         * debit-card transactions
         * offers
         * bills
         * declined transactions
         * investments
         * insurance
         * etc.
         */
        val exclusionReason = ExclusionRules.check(text)

        if (exclusionReason != null) {
            return ClassificationResult.Exclude(
                reason = exclusionReason,
                confidence = 0.95
            )
        }

        /*
         * 4. Normal credit-card transaction.
         */
        if (InclusionRules.isRelevantCreditCardTransaction(text)) {
            return ClassificationResult.Include
        }

        /*
         * 5. Conservative fallback.
         */
        return ClassificationResult.Exclude(
            reason = ExcludeReason.LOW_CONFIDENCE,
            confidence = 0.60
        )
    }

    private fun normalize(sms: String): String {
        return sms
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")
    }
}