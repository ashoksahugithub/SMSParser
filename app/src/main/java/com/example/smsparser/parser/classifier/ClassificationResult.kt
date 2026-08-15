package com.example.smsparser.parser.classifier

import com.example.smsparser.model.ExcludeReason

sealed class ClassificationResult {

    data class Exclude(
        val reason: ExcludeReason,
        val confidence: Double = 0.95
    ) : ClassificationResult()

    data object Include : ClassificationResult()
}