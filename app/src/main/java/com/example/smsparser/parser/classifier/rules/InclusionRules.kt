package com.example.smsparser.parser.classifier.rules

object InclusionRules {

    /**
     * Normal credit-card transaction.
     *
     * Examples:
     *
     * "INR 1250 spent on HDFC Bank Credit Card xx5678 at SWIGGY"
     *
     * "Spent Rs 1200 on YES BANK Credit Card XX8888 at AMAZON"
     *
     * "spent Rs 849 at Blackwater Coffee with your
     *  BOBCARD One Credit Card ending in XX9907"
     */
    fun isRelevantCreditCardTransaction(text: String): Boolean {

        val hasCreditCard = Regex(
            """\bcredit\s*card\b"""
        ).containsMatchIn(text)

        val hasTransactionAction =
            text.contains("spent") ||
                    text.contains("spend") ||
                    text.contains("debited") ||
                    text.contains("charged") ||
                    text.contains("purchase") ||
                    text.contains("transaction")

        return hasCreditCard && hasTransactionAction
    }

    /**
     * Explicit card refund.
     *
     * Important:
     *
     * We intentionally require CARD identity.
     *
     * Therefore:
     *
     * Refund -> HDFC Card       => INCLUDE
     * Refund -> Credit Card     => INCLUDE
     * Refund -> Debit Card      => INCLUDE
     *
     * But:
     *
     * Refund -> A/c             => NOT matched
     * Refund -> Account         => NOT matched
     * Refund -> UPI             => NOT matched
     */
    fun isCardRefund(text: String): Boolean {

        val isRefund =
            text.contains("refund") ||
                    text.contains("refunded") ||
                    text.contains("reversal")

        if (!isRefund) {
            return false
        }

        val hasCardIdentity =
            Regex("""\bcredit\s*card\b""").containsMatchIn(text) ||
                    Regex("""\bdebit\s*card\b""").containsMatchIn(text) ||
                    Regex("""\bcard\b""").containsMatchIn(text)

        val hasBankAccountIdentity =
            text.contains("a/c") ||
                    text.contains("account")

        val hasUpiIdentity =
            text.contains("upi") ||
                    text.contains("via upi")

        return hasCardIdentity &&
                !hasBankAccountIdentity &&
                !hasUpiIdentity
    }
}