package com.example.smsparser.parser

import com.example.smsparser.model.Decision
import com.example.smsparser.model.ParsedResult
import com.example.smsparser.model.Transaction
import com.example.smsparser.parser.classifier.ClassificationResult
import com.example.smsparser.parser.classifier.SmsClassifier
import com.example.smsparser.parser.confidence.ConfidenceCalculator
import com.example.smsparser.parser.extractor.AmountExtractor
import com.example.smsparser.parser.extractor.BankResolver
import com.example.smsparser.parser.extractor.CardExtractor
import com.example.smsparser.parser.extractor.CurrencyExtractor
import com.example.smsparser.parser.extractor.DateExtractor
import com.example.smsparser.parser.extractor.MerchantExtractor
import com.example.smsparser.parser.extractor.TransactionTypeExtractor

class SmsParser(
    private val classifier: SmsClassifier = SmsClassifier(),
    private val amountExtractor: AmountExtractor = AmountExtractor(),
    private val currencyExtractor: CurrencyExtractor = CurrencyExtractor(),
    private val bankResolver: BankResolver = BankResolver(),
    private val cardExtractor: CardExtractor = CardExtractor(),
    private val merchantExtractor: MerchantExtractor = MerchantExtractor(),
    private val dateExtractor: DateExtractor = DateExtractor(),
    private val transactionTypeExtractor: TransactionTypeExtractor =
        TransactionTypeExtractor(),
    private val confidenceCalculator: ConfidenceCalculator =
        ConfidenceCalculator()
) {

    fun parse(
        sms: String,
        id: Int
    ): ParsedResult {

        val classification = classifier.classify(sms)

        return when (classification) {

            is ClassificationResult.Exclude -> {
                ParsedResult(
                    id = id,
                    rawSms = sms,
                    decision = Decision.EXCLUDE,
                    excludeReason = classification.reason,
                    transaction = null,
                    confidence = 0.95
                )
            }

            ClassificationResult.Include -> {
                parseTransaction(
                    sms = sms,
                    id = id
                )
            }
        }
    }

    private fun parseTransaction(
        sms: String,
        id: Int
    ): ParsedResult {

        val amount = amountExtractor.extract(sms)
        val currency = currencyExtractor.extract(sms)
        val bank = bankResolver.resolve(sms)
        val card = cardExtractor.extractLastFour(sms)
        val merchant = merchantExtractor.extract(sms)
        val date = dateExtractor.extract(sms)
        val type = transactionTypeExtractor.extract(sms)

        val transaction = Transaction(
            amount = amount ?: 0.0,
            currency = currency,
            bankName = bank ?: "UNKNOWN",
            cardLastFour = card,
            merchantName = merchant,
            date = date,
            type = type
        )

        val confidence = confidenceCalculator.calculate(
            amount = amount,
            bank = bank,
            card = card,
            merchant = merchant,
            date = date
        )

        return ParsedResult(
            id = id,
            rawSms = sms,
            decision = Decision.INCLUDE,
            excludeReason = null,
            transaction = transaction,
            confidence = confidence
        )
    }
}