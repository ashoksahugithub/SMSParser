# Bank SMS Parser

Bank SMS Parser is an Android application that parses bank and credit card SMS messages to categorize and track financial transactions.

The solution is implemented using **Android Native development with Kotlin**. The parser uses a rule-based classification pipeline along with regular expressions and dedicated extractors to identify relevant credit-card transactions and extract information such as amount, merchant, bank, date, currency, card details, and transaction type.

The implementation also aggressively filters irrelevant messages such as OTPs, promotional messages, declined transactions, debit-card transactions, balance alerts, investments, insurance, bill reminders, fees, and other excluded categories.

---

## Project Structure

```text
app/src/main/java/com/example/smsparser/
│
├── model/
│   ├── Transaction.kt
│   ├── TransactionType.kt
│   ├── ParsedResult.kt
│   ├── Decision.kt
│   └── ExcludeReason.kt
│
├── parser/
│   ├── SmsParser.kt
│   ├── classifier/
│   │   ├── SmsClassifier.kt
│   │   ├── ClassificationResult.kt
│   │   └── rules/
│   │       ├── ExclusionRules.kt
│   │       └── InclusionRules.kt
│   │
│   ├── config/
│   │   ├── BankConfig.kt
│   │   ├── CardProductConfig.kt
│   │   └── ParserConfig.kt
│   │
│   ├── extractor/
│   │   ├── AmountExtractor.kt
│   │   ├── BankResolver.kt
│   │   ├── CardExtractor.kt
│   │   ├── CurrencyExtractor.kt
│   │   ├── DateExtractor.kt
│   │   ├── MerchantExtractor.kt
│   │   └── TransactionTypeExtractor.kt
│   │
│   └── confidence/
│       └── ConfidenceCalculator.kt
│
├── repository/
├── ui/
└── MainActivity.kt

app/src/test/java/
└── Parser unit tests
```

---

## High-Level Flow

```text
SMS
 │
 ▼
Normalization
 │
 ▼
SmsClassifier
 │
 ├── EXCLUDE ───────────────► ParsedResult(EXCLUDE)
 │
 └── INCLUDE
       │
       ▼
   Extractors
       │
       ├── Amount
       ├── Currency
       ├── Bank
       ├── Card
       ├── Merchant
       ├── Date
       └── Transaction Type
       │
       ▼
   Confidence Calculation
       │
       ▼
   ParsedResult(INCLUDE)
       │
       ▼
   Android UI
```

The classifier is deliberately kept separate from extraction. This prevents an SMS from being treated as a transaction merely because it contains a financial amount.

---

## Key Features

- Parses bank and credit-card SMS messages.
- Extracts transaction amount and currency.
- Resolves bank names and aliases to canonical names.
- Extracts card last-four digits where available.
- Extracts merchant names from different SMS structures.
- Extracts transaction dates.
- Identifies transaction types such as:
  - `DEBIT`
  - `CREDIT`
  - `REFUND`
- Classifies messages into `INCLUDE` or `EXCLUDE`.
- Provides an exclusion reason for filtered SMS messages.
- Calculates a confidence score for parsed transactions.
- Displays parsed transactions and SMS details in a native Android UI.
- Allows the user to click a transaction/SMS card and view its complete details.
- Uses Kotlin and Android Native/Jetpack Compose rather than a cross-platform implementation.

---

## Important Parsing Challenges

The sample SMS set contains several different sentence structures. A major implementation challenge was avoiding overfitting the parser to the exact 25 examples.

### 1. Merchant extraction — different sentence structures

For example:

- `spent ... at AMAZON ...`
- `spent ... to HOSPITALITY PVT DELHI IN on your ... Credit Card`
- `spent ... at Blackwater Coffee, Gurgaon with your ... Credit Card`

The merchant extractor therefore uses structural boundaries such as:

- `at ... on`
- `at ... with`
- `to ... on ... Card`
- sentence boundaries

rather than matching only specific merchant names.

This was especially important for cases such as **2, 7, 8, and 9**, where a generic merchant extraction rule could accidentally include card/bank information in the merchant title.

### 2. Bank resolution

Bank names can appear in different forms:

- `HDFC Bank`
- `Axis Bank`
- `Edge Federal Bank`
- `BOBCARD`
- `Jupiter`
- other bank/card product aliases

The parser separates bank resolution from merchant extraction so that a card product or bank alias does not accidentally become part of the merchant name.

For example, the merchant in the Blackwater Coffee example should remain:

```text
Blackwater Coffee, Gurgaon
```

while the bank/card issuer information is resolved separately.

### 3. Refund detection

Refunds require special handling because the presence of `credited` alone is not sufficient to classify a transaction as a normal credit.

For example:

```text
Refund of Rs 450.00 has been credited to your HDFC Card xx5678
from BIGBASKET...
```

This must be classified as:

```text
TransactionType.REFUND
```

and not simply:

```text
TransactionType.CREDIT
```

The refund keyword therefore has higher priority than generic credit keywords.

The implementation also distinguishes a **card refund** from a generic bank/UPI credit based on the card context.

---

# Running Tests

The core parsing logic is covered by Kotlin unit tests for different SMS structures, including normal spends, refunds, foreign currency transactions, and excluded messages.

Run all tests:

```bash
./gradlew test
```

Run Debug unit tests:

```bash
./gradlew testDebugUnitTest
```

### Viewing Test Results

After the tests complete, the HTML report can be found at:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

---

# 5. What I Would Do Differently With a Full Week

The current implementation focuses on delivering a working, modular parser within the available scope. With a full week, the following areas would be improved further.

### Parser improvements

- Expand the rule engine with more variations of real-world Indian bank SMS templates.
- Add more robust handling for malformed and partially received SMS messages.
- Improve merchant extraction using a scoring/ranking approach instead of relying only on the first matching regex.
- Improve date parsing for additional date formats and ambiguous dates.
- Add stronger handling for multiple monetary values in a single SMS, such as:
  - transaction amount
  - GST
  - available balance
  - available credit limit
- Improve bank/card-product normalization using a more maintainable configuration-driven mapping.
- Add more comprehensive confidence scoring based on field consistency rather than only field presence.

### Testing improvements

- Increase test coverage substantially.
- Add parameterized tests for families of SMS templates.
- Add negative tests specifically designed to catch false positives.
- Add regression tests for every parser bug discovered during development.
- Test combinations of:
  - bank + card
  - UPI + bank account
  - card refund
  - card payment
  - balance alerts
  - promotional messages
  - malformed SMS

### Android improvements

- Add proper navigation instead of maintaining screen state manually.
- Introduce dependency injection for parser/repository components.
- Add persistent local storage for parsed transactions.
- Add proper loading/error/empty states.
- Improve UI accessibility and responsive layouts.
- Add sorting/filtering/search for transactions.
- Add instrumentation/UI tests in addition to unit tests.

### Production-readiness

Before production deployment, the parser should be validated against a much larger anonymized dataset of real SMS formats. The most important metric would not simply be extraction accuracy, but the balance between:

- **false inclusion** — irrelevant SMS incorrectly treated as transactions
- **false exclusion** — valid transactions incorrectly discarded

---

# 6. Production Android Design Note

The current implementation is intentionally kept simple and focused on the parsing assignment.

For a production Android application, the architecture would be evolved toward a clean, testable layered design:

```text
                 ┌─────────────────────┐
                 │      UI Layer       │
                 │ Jetpack Compose     │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │     ViewModel       │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Repository       │
                 └──────────┬──────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
      ┌───────────────┐           ┌────────────────┐
      │ SMS Data      │           │ Local Database │
      │ Source        │           │ Room           │
      └───────────────┘           └────────────────┘
              │
              ▼
      ┌────────────────┐
      │ Parser Engine  │
      │ Classifier +   │
      │ Extractors     │
      └────────────────┘
```

### Production considerations

**SMS ingestion**

A production implementation would read SMS through the appropriate Android SMS APIs and permissions rather than relying only on sample JSON data.

**Persistence**

The current assignment does not require persistent storage for the parser demonstration. For production, parsed transactions should be persisted locally, and **Room** would be a suitable choice.

**Background processing**

SMS parsing should not block the main thread. Parsing, persistence, and potentially large batches of SMS should be performed using Kotlin Coroutines.

**Dependency injection**

Components such as `SmsParser`, classifiers, extractors, repositories, and database dependencies can be provided through a DI framework such as Hilt.

**Security and privacy**

Financial SMS data is sensitive. A production implementation should:

- Minimize SMS data retention.
- Avoid unnecessary logging of raw SMS.
- Never expose sensitive SMS content in production logs.
- Store only required transaction information.
- Follow Android permission and privacy requirements.
- Clearly communicate why SMS access is required.

**Scalability**

The parser should remain independent of the UI and storage layer so that the same parsing engine can be tested independently and reused by different data sources.

---

# 7. AI Tool Usage

AI tools were used as a development aid during the implementation. AI tool-ChatGPT and Gemini

The AI-assisted workflow was primarily used for:

- Structuring the parser into separate classifier, rule, extractor, configuration, and confidence components.
- Reviewing Kotlin/Android code.
- Identifying compilation errors and suggesting fixes.
- Generating and refining regular expressions.
- Reviewing edge cases in SMS parsing.
- Improving merchant extraction for different SMS sentence structures.
- Reviewing classification logic against the provided sample messages.
- Improving the Android UI implementation.
- Generating/refining unit-test scenarios.
- Reviewing the project structure and README documentation.

### Important implementation principle

AI-generated suggestions were **reviewed, tested, and adapted** rather than being blindly copied into the project.

The final implementation was kept:

- Kotlin-based
- Android Native
- modular
- unit-testable
- rule/configuration driven
- independent of the UI layer

The parser was also iteratively tested against the provided SMS examples, especially the challenging cases involving merchant extraction, bank/card identification, and refunds.

---

# Technology Stack

- **Language:** Kotlin
- **Platform:** Android Native
- **UI:** Jetpack Compose
- **Architecture:** ViewModel + Repository + Parser layers
- **Asynchronous programming:** Kotlin Coroutines / Flow
- **Testing:** Kotlin/JUnit unit tests
- **Parsing:** Kotlin Regex + rule-based classification
- **Persistence:** Not required for the current assignment; Room is a production consideration

---

# Design Principles

The implementation follows these principles:

1. **Separate classification from extraction.**
2. **Prefer generic parsing rules over sample-specific patches.**
3. **Keep parser components independently testable.**
4. **Give exclusion rules priority over inclusion.**
5. **Give specific transaction types such as REFUND priority over generic CREDIT detection.**
6. **Keep bank/card resolution separate from merchant extraction.**
7. **Preserve the raw SMS for debugging/detail display.**
8. **Use confidence scoring to represent extraction reliability.**
9. **Keep the core parser independent from Android UI concerns.**
10. **Use Android Native Kotlin as the implementation platform.**

---

# Conclusion

The project demonstrates a modular Android Native SMS parsing solution that separates:

```text
Classification
      ↓
Extraction
      ↓
Transaction Modeling
      ↓
Confidence Calculation
      ↓
UI Presentation
```

The primary focus is not simply extracting values from the provided examples, but building a rule-based structure that can be extended to handle additional bank SMS formats without adding a separate hardcoded patch for every message.
