package com.example.smsparser.parser

import com.example.smsparser.model.Decision
import com.example.smsparser.model.ExcludeReason
import com.example.smsparser.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsParserTest {

    private val parser = SmsParser()

    @Test
    fun `test clear credit-card spend`() {
        val sms = "INR 1,250.00 spent on HDFC Bank Credit Card xx5678 at SWIGGY on 03-04-2026. Avl Limit: INR 1,45,300.00."
        val result = parser.parse(sms, 1)

        assertEquals(Decision.INCLUDE, result.decision)
        assertNull(result.excludeReason)
        assertNotNull(result.transaction)

        result.transaction?.let {
            assertEquals(1250.00, it.amount, 0.0)
            assertEquals("INR", it.currency)
            assertEquals("HDFC Bank", it.bankName)
            assertEquals("5678", it.cardLastFour)
            assertEquals("SWIGGY", it.merchantName)
            assertEquals("2026-04-03", it.date)
            assertEquals(TransactionType.DEBIT, it.type)
        }
        assertTrue("Confidence should be reasonably high", result.confidence > 0.8)
    }

    @Test
    fun `test debit-card exclusion`() {
        val sms = "Transaction Alert: Rs. 500.00 debited from your HDFC Bank Debit Card ending 1234 at SWIGGY on 06-04-26."
        val result = parser.parse(sms, 2)

        assertEquals(Decision.EXCLUDE, result.decision)
        assertEquals(ExcludeReason.DEBIT_CARD, result.excludeReason)
        assertNull(result.transaction)
    }

    @Test
    fun `test OTP exclusion`() {
        val sms = "Use 458219 as your OTP for HDFC Bank Net Banking login. Valid for 5 mins. Do NOT share with anyone."
        val result = parser.parse(sms, 3)

        assertEquals(Decision.EXCLUDE, result.decision)
        assertEquals(ExcludeReason.OTP, result.excludeReason)
        assertNull(result.transaction)
    }

    @Test
    fun `test UPI exclusion`() {
        val sms = "Rs.99 debited from A/c XX4521 via UPI on 14-04-26. UPI Ref: 240478234511 to NETFLIX-MONTHLY."
        val result = parser.parse(sms, 4)

        assertEquals(Decision.EXCLUDE, result.decision)
        assertEquals(ExcludeReason.UPI_BANK_ACCOUNT, result.excludeReason)
        assertNull(result.transaction)
    }

    @Test
    fun `test fintech co-branded issuer attribution`() {
        val sms = "You've spent Rs. 849.00 at Blackwater Coffee, Gurgaon with your BOBCARD One Credit Card ending in XX9907 on 08-04-2026."
        val result = parser.parse(sms, 5)

        assertEquals(Decision.INCLUDE, result.decision)
        assertNotNull(result.transaction)
        assertEquals("Bank of Baroda", result.transaction?.bankName)
        assertEquals("9907", result.transaction?.cardLastFour)
    }

    @Test
    fun `test refund transaction`() {
        val sms = "Refund of Rs 450.00 has been credited to your HDFC Card xx5678 from BIGBASKET on 12-04-26 against original txn dated 02-04-26."
        val result = parser.parse(sms, 6)

        assertEquals(Decision.INCLUDE, result.decision)
        assertNotNull(result.transaction)
        assertEquals(TransactionType.REFUND, result.transaction?.type)
        assertEquals(450.00, result.transaction?.amount ?: 0.0, 0.0)
    }

    @Test
    fun `test foreign-currency transaction`() {
        val sms = "USD 49.99 spent on your Axis Bank Credit Card XX9876 at NETFLIX.COM/US on 13-APR-26. Foreign currency markup of 3.5% will be applied."
        val result = parser.parse(sms, 7)

        assertEquals(Decision.INCLUDE, result.decision)
        assertNotNull(result.transaction)
        assertEquals(49.99, result.transaction?.amount ?: 0.0, 0.0)
        assertEquals("USD", result.transaction?.currency)
    }

    @Test
    fun `test malformed SMS`() {
        // Blank or completely empty text triggers MALFORMED_SMS
        val result = parser.parse("   ", 8)

        assertEquals(Decision.EXCLUDE, result.decision)
        assertEquals(ExcludeReason.MALFORMED_SMS, result.excludeReason)
        assertNull(result.transaction)
    }
}
