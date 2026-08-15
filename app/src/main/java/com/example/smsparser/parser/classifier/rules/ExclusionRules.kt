package com.example.smsparser.parser.classifier.rules

import com.example.smsparser.model.ExcludeReason
import com.example.smsparser.parser.config.ParserConfig

object ExclusionRules {

    fun check(text: String): ExcludeReason? {

        return when {

            isOtp(text) ->
                ExcludeReason.OTP

            isDeclined(text) ->
                ExcludeReason.DECLINED

            isFutureAutoDebit(text) ->
                ExcludeReason.FUTURE_AUTO_DEBIT

            isBillDue(text) ->
                ExcludeReason.BILL_DUE

            isCardPayment(text) ->
                ExcludeReason.CARD_PAYMENT

            isFeeOrCharge(text) ->
                ExcludeReason.FEE_OR_CHARGE

            isEmiConversion(text) ->
                ExcludeReason.EMI_CONVERSION

            isInvestment(text) ->
                ExcludeReason.INVESTMENT

            isInsurance(text) ->
                ExcludeReason.INSURANCE

            isOffer(text) ->
                ExcludeReason.OFFER

            isDebitCard(text) ->
                ExcludeReason.DEBIT_CARD

            isUpiBankAccount(text) ->
                ExcludeReason.UPI_BANK_ACCOUNT

            isSavingsAccount(text) ->
                ExcludeReason.SAVINGS_ACCOUNT

            isBalanceAlert(text) ->
                ExcludeReason.BALANCE_ALERT

            else -> null
        }
    }

    private fun isOtp(text: String): Boolean {
        return text.contains("otp") ||
                text.contains("one time password")
    }

    private fun isDeclined(text: String): Boolean {
        return text.contains("declined") ||
                text.contains("transaction failed")
    }

    private fun isFutureAutoDebit(text: String): Boolean {
        return text.contains("will be auto debited") ||
                text.contains("will be debited") ||
                text.contains("e-mandate")
    }

    private fun isBillDue(text: String): Boolean {
        return text.contains("bill") &&
                text.contains("due")
    }

    /**
     * Payment made towards the credit-card bill.
     *
     * Example:
     * "Payment of Rs 23,450 received towards your HDFC
     *  Bank Credit Card..."
     */
    private fun isCardPayment(text: String): Boolean {
        return (
                text.contains("payment received") ||
                        text.contains("payment of")
                ) &&
                text.contains("credit card")
    }

    private fun isFeeOrCharge(text: String): Boolean {
        return text.contains("finance charge") ||
                text.contains("late payment fee")
    }

    private fun isEmiConversion(text: String): Boolean {
        return text.contains("converted to emi") ||
                text.contains("converted into emi") ||
                text.contains("converted to an emi")
    }

    private fun isInvestment(text: String): Boolean {
        return text.contains("sip") ||
                text.contains("mutual fund") ||
                text.contains("investment")
    }

    private fun isInsurance(text: String): Boolean {
        return text.contains("insurance") ||
                (
                        text.contains("premium") &&
                                text.contains("policy")
                        )
    }

    private fun isOffer(text: String): Boolean {

        val promotional =
            text.contains("flat") ||
                    text.contains("offer") ||
                    text.contains("t&c apply") ||
                    text.contains("terms and conditions") ||
                    text.contains("visit hdfcbank.com/offers")

        val hasCompletedTransaction =
            text.contains("spent") ||
                    text.contains("debited") ||
                    text.contains("charged") ||
                    text.contains("purchase")

        return promotional && !hasCompletedTransaction
    }

    private fun isDebitCard(text: String): Boolean {
        return text.contains("debit card") ||
                text.contains("debitcard")
    }

    private fun isUpiBankAccount(text: String): Boolean {

        val hasUpi =
            text.contains("via upi") ||
                    text.contains("upi/")

        val hasBankAccount =
            text.contains("a/c") ||
                    text.contains("acct") ||
                    text.contains("account")

        return hasUpi && hasBankAccount
    }

    private fun isSavingsAccount(text: String): Boolean {

        val hasAccountReference =
            text.contains("a/c") ||
                    text.contains("acct") ||
                    text.contains("account")

        val hasTransaction =
            text.contains("debited") ||
                    text.contains("credited") ||
                    text.contains("sent") ||
                    text.contains("spent")

        val hasCreditCardIdentity =
            text.contains("credit card") ||
                    text.contains("creditcard")

        return hasAccountReference &&
                hasTransaction &&
                !hasCreditCardIdentity
    }

    private fun isBalanceAlert(text: String): Boolean {

        val hasBalanceKeywords =
            text.contains("avl bal") ||
                    text.contains("available balance") ||
                    text.contains("available limit") ||
                    text.contains("avl lmt")

        val hasTransactionKeywords =
            text.contains("debited") ||
                    text.contains("credited") ||
                    text.contains("spent") ||
                    text.contains("sent") ||
                    text.contains("charged")

        return hasBalanceKeywords && !hasTransactionKeywords
    }
}