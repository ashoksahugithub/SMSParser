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

        // 2. Exclusion rules always have priority
        val exclusionReason = ExclusionRules.check(text)

        if (exclusionReason != null) {
            return ClassificationResult.Exclude(
                reason = exclusionReason,
                confidence = 0.95
            )
        }

        // 3. Explicit positive identification
        if (InclusionRules.isRelevantCreditCardTransaction(text)) {
            return ClassificationResult.Include
        }

        // 4. Conservative fallback
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