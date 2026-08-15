# Bank SMS Parser

Bank SMS Parser is an Android application that parses bank and credit-card SMS messages and converts relevant messages into structured financial transactions.

The application uses a rule-based parsing pipeline to:

- classify SMS messages as included or excluded
- extract transaction amounts and currencies
- identify banks and card issuers
- extract card last-four digits
- identify merchants
- extract transaction dates
- determine transaction type
- calculate parsing confidence
- provide detailed information for every parsed SMS

The project is implemented using **native Android development with Kotlin and Jetpack Compose**.

---

## Features

- Credit-card transaction detection
- Merchant extraction using multiple SMS formats
- Bank and card issuer resolution
- Configurable bank aliases
- Configurable card-product mappings
- Refund detection
- Foreign-currency transaction support
- Transaction date extraction
- Card last-four extraction
- Confidence scoring
- Rule-based SMS exclusion
- OTP filtering
- Debit-card filtering
- UPI bank-account filtering
- Promotional SMS filtering
- Declined transaction filtering
- Bill-due filtering
- Investment and insurance filtering
- EMI conversion filtering
- Balance-alert filtering
- Malformed SMS handling
- Raw SMS inspection through a detail screen

---

## Technology Stack

- Kotlin
- Native Android
- Jetpack Compose
- Material 3
- Kotlin Coroutines / Flow
- Android ViewModel
- JUnit
- Regular Expressions

The parser itself is kept independent of Android UI concerns as much as possible, making the core parsing logic easier to test.

---

## Project Structure

```text
app/src/main/java/com/example/smsparser/

├── model/
│   ├── Decision.kt
│   ├── ExcludeReason.kt
│   ├── ParsedResult.kt
│   ├── Transaction.kt
│   └── TransactionType.kt
│
├── parser/
│   ├── SmsParser.kt
│   │
│   ├── classifier/
│   │   ├── SmsClassifier.kt
│   │   ├── ClassificationResult.kt
│   │   └── rules/
│   │       ├── ExclusionRules.kt
│   │       └── InclusionRules.kt
│   │
│   ├── config/
│   │   ├── ParserConfig.kt
│   │   ├── BankConfig.kt
│   │   └── CardProductConfig.kt
│   │
│   ├── confidence/
│   │   └── ConfidenceCalculator.kt
│   │
│   └── extractor/
│       ├── AmountExtractor.kt
│       ├── BankResolver.kt
│       ├── CardExtractor.kt
│       ├── CurrencyExtractor.kt
│       ├── DateExtractor.kt
│       ├── MerchantExtractor.kt
│       └── TransactionTypeExtractor.kt
│
├── repository/
│   └── SmsRepository.kt
│
└── ui/
    ├── TransactionListScreen.kt
    ├── TransactionViewModel.kt
    └── TransactionViewModelFactory.kt
```

---

## Parsing Architecture

The parser follows a pipeline-based approach:

```text
Raw SMS
   ↓
SmsClassifier
   ↓
Include / Exclude
   ↓
Extractors
   ├── Amount
   ├── Currency
   ├── Bank
   ├── Card
   ├── Merchant
   ├── Date
   └── Transaction Type
   ↓
ConfidenceCalculator
   ↓
ParsedResult
```

Excluded messages do not proceed through transaction extraction.

For included messages, the parser extracts the available transaction fields and calculates a confidence score based on the extracted information.

---

## Configuration-Driven Bank and Card Resolution

Bank and card-product identification is configuration-driven rather than being tightly coupled to individual SMS examples.

The configuration layer contains:

```text
ParserConfig
├── BankConfig
└── CardProductConfig
```

### BankConfig

`BankConfig` represents a canonical bank name and its supported aliases.

For example:

```kotlin
BankConfig(
    canonicalName = "New Bank",
    aliases = listOf(
        "new bank",
        "newbank"
    )
)
```

This allows different SMS representations of the same bank to resolve to a single canonical name.

### CardProductConfig

`CardProductConfig` allows card-product or co-branded card identifiers to be associated with their underlying issuer.

This is important for cases such as:

```text
BOBCARD One Credit Card
```

where the SMS may identify the card product rather than explicitly stating the issuer as:

```text
Bank of Baroda
```

The configuration layer allows these mappings to be extended without changing the core parser logic.

### Custom configuration

The parser components can receive a custom `ParserConfig`, for example:

```kotlin
val customConfig = ParserConfig(
    banks = listOf(
        BankConfig(
            canonicalName = "New Bank",
            aliases = listOf(
                "new bank",
                "newbank"
            )
        )
    ),
    cardProducts = emptyList()
)

val parser = SmsParser(
    bankResolver = BankResolver(customConfig)
)
```

This makes the parser extensible to new banks and card products without modifying the extraction algorithm itself.

---

## Important SMS Formats and Challenges

The parser was designed against multiple real-world SMS formats rather than relying on a single fixed template.

### Merchant extraction

Merchant information can appear in different positions.

For example:

```text
INR 1,250.00 spent on HDFC Bank Credit Card xx5678 at SWIGGY
```

and:

```text
You've spent Rs. 849.00 at Blackwater Coffee, Gurgaon
with your BOBCARD One Credit Card ending in XX9907
```

and:

```text
You've spent Rs 1836.00 to HOSPITALITY PVT DELHI IN
on your Edge Federal Bank Credit Card ending 4422
```

The merchant extractor therefore uses multiple patterns and sentence boundaries rather than depending on one exact SMS template.

### Challenge: SMS 2 and SMS 7

Some SMS formats contain merchant and date information close together:

```text
at SWIGGY on 03-04-2026
```

or:

```text
at AMAZON on 07-04-26
```

A naive merchant extraction could incorrectly produce:

```text
AMAZON on 07-04-26
```

instead of:

```text
AMAZON
```

The extractor therefore explicitly treats date boundaries such as `on <date>` as the end of the merchant name.

### Challenge: SMS 8

Merchant information can appear after the word `to`:

```text
spent Rs 1836.00 to HOSPITALITY PVT DELHI IN
on your Edge Federal Bank Credit Card
```

The parser therefore supports both `at` and `to` merchant patterns.

Expected merchant:

```text
HOSPITALITY PVT DELHI IN
```

### Challenge: SMS 9

Some SMS messages contain a merchant name with punctuation and location information:

```text
at Blackwater Coffee, Gurgaon
with your BOBCARD One Credit Card
```

The complete merchant value should be retained:

```text
Blackwater Coffee, Gurgaon
```

At the same time, the card-product configuration resolves:

```text
BOBCARD One
```

to:

```text
Bank of Baroda
```

This demonstrates why merchant extraction and bank/card-product resolution are treated as separate responsibilities.

### Challenge: SMS 21 - Refunds

Refund messages require special handling because a refund is not a normal debit.

Example:

```text
Refund of Rs 450.00 has been credited to your HDFC Card xx5678
from BIGBASKET on 12-04-26
```

The parser identifies:

```text
Merchant: BIGBASKET
Amount: 450.00
Currency: INR
Bank: HDFC Bank
Card: 5678
Type: REFUND
```

Refunds are included because they represent a financial transaction relevant to the user's credit-card activity.

The refund is treated differently from a normal bank-account or UPI credit.

---

## Exclusion Rules

The classifier intentionally excludes messages that should not be treated as regular credit-card transactions.

Examples include:

- OTP messages
- declined transactions
- future auto-debits
- bill-due notifications
- credit-card payment confirmations
- finance charges / fees
- EMI conversion notifications
- investments / SIPs
- insurance payments
- promotional offers
- debit-card transactions
- UPI bank-account transactions
- savings-account transactions
- balance-only alerts
- malformed messages

The classifier uses exclusion rules before positive transaction identification so that known irrelevant categories are filtered out early.

---

## Confidence Calculation

The parser calculates confidence using the availability of important transaction fields.

The current scoring considers:

| Field | Weight |
|---|---:|
| Amount | 25% |
| Bank | 20% |
| Card | 20% |
| Merchant | 20% |
| Date | 15% |

The final confidence score is constrained between `0.0` and `1.0`.

This allows the UI to communicate how confidently a transaction was parsed.

---

## UI

The application uses **Jetpack Compose** for the UI.

The main screen provides:

- total INR debit amount
- total INR credit/refund amount
- included transaction list
- excluded SMS list
- exclusion reasons
- confidence indicators
- merchant
- bank
- amount
- currency
- date
- transaction type

Tapping a row opens a detail screen containing:

- raw SMS
- decision
- exclusion reason, if applicable
- parsed transaction fields
- confidence

The UI is intentionally kept clean and readable because the primary evaluation focus is parsing correctness.

---

## Testing

Unit tests cover:

- normal credit-card spends
- debit-card exclusions
- OTP exclusions
- UPI exclusions
- co-branded card issuer resolution
- refunds
- foreign-currency transactions
- malformed SMS
- custom bank configuration

A configuration test verifies that bank resolution is actually configuration-driven:

```kotlin
val customConfig = ParserConfig(
    banks = listOf(
        BankConfig(
            canonicalName = "New Bank",
            aliases = listOf(
                "new bank",
                "newbank"
            )
        )
    ),
    cardProducts = emptyList()
)

val parser = SmsParser(
    bankResolver = BankResolver(customConfig)
)
```

---

## Running Tests

Run all tests using:

```bash
./gradlew test
```

Or specifically run Debug unit tests:

```bash
./gradlew testDebugUnitTest
```

Test reports are generated at:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## What I Would Do Differently With a Full Week

With additional development time, the following improvements would be made:

### 1. Expand SMS template coverage

Build a larger anonymized corpus of SMS formats from different banks, card issuers, and transaction types.

This would allow the rule engine and extractors to be validated against more variations instead of a relatively small assignment dataset.

### 2. Improve configuration management

Move bank aliases and card-product mappings into a more maintainable configuration source rather than requiring code changes for every new mapping.

For production, this could be backed by a remotely versioned configuration with safe local fallback.

### 3. Improve merchant extraction

Merchant extraction could be enhanced with a more structured tokenization strategy and additional boundary rules for bank-specific templates.

### 4. Improve confidence scoring

The current confidence calculation is field-presence based.

A production implementation could also consider:

- strength of the matched pattern
- consistency between extracted fields
- known bank/card-product matches
- transaction-type certainty
- ambiguity between multiple possible merchants

### 5. Add broader test coverage

Add parameterized tests covering:

- more bank SMS templates
- different date formats
- currencies
- merchant names containing punctuation
- refunds and reversals
- co-branded cards
- malformed/truncated messages
- conflicting SMS signals

### 6. Production hardening

For production use, parsing should be monitored using anonymized parsing metrics and failures should be fed back into the configuration/rule system.

---

## Production Android Design Note

For a production Android application, the current architecture can be extended while keeping the parser independent from the UI.

A possible architecture would be:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Parser
 ├── Classifier
 ├── Extractors
 ├── Configuration
 └── Confidence Calculator
```

The parser remains a pure Kotlin component and can therefore be tested independently of Android.

For production:

- SMS ingestion would be separated from parsing.
- Parsed transactions could be persisted using Room.
- Configuration could be versioned and cached locally.
- Parser configuration could be updated without requiring an application release.
- Parsing failures could be logged using privacy-safe, anonymized telemetry.
- UI state would remain managed by ViewModel and Kotlin Flow.
- Long-running or batch parsing would be performed off the main thread.

The current implementation intentionally keeps the assignment scope smaller while maintaining a structure that can be extended toward a production architecture.

---

## AI Tool Usage

AI tools(ChatGPT and Gemini) were used as development assistants during the implementation.

They were used for activities such as:

- brainstorming parser architecture
- reviewing Kotlin code
- identifying edge cases in SMS formats
- improving regular expressions
- suggesting unit-test scenarios
- reviewing Compose UI structure
- identifying potential bugs and classification conflicts
- improving documentation

AI-generated suggestions were **reviewed, tested, and adapted manually** before being incorporated into the project.

The final parsing behavior, configuration integration, tests, and architectural decisions were validated against the assignment requirements and test cases.

AI tools were used to accelerate development and code review, not as a replacement for testing or engineering decisions.

---

## Native Android Implementation

This project was implemented as a **native Android application using Kotlin and Jetpack Compose**.

The core parser is written in Kotlin, while the UI uses Jetpack Compose and Android ViewModel/Flow for state management.

No cross-platform UI framework was used.
