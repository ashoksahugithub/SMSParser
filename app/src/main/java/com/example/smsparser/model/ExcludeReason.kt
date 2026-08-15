package com.example.smsparser.model

enum class ExcludeReason {

    OTP,
    DECLINED,
    FUTURE_AUTO_DEBIT,
    BILL_DUE,
    CARD_PAYMENT,
    FEE_OR_CHARGE,
    EMI_CONVERSION,
    INVESTMENT,
    INSURANCE,
    OFFER,
    DEBIT_CARD,
    UPI_BANK_ACCOUNT,
    SAVINGS_ACCOUNT,
    BALANCE_ALERT,
    MALFORMED_SMS,
    LOW_CONFIDENCE
}