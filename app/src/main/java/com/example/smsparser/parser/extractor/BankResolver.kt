package com.example.smsparser.parser.extractor

import com.example.smsparser.parser.config.ParserConfig

class BankResolver(
    private val config: ParserConfig = ParserConfig.DEFAULT
) {

    fun resolve(sms: String): String? {

        val text = normalize(sms)

        if (text.isBlank()) {
            return null
        }

        /*
         * Product-specific patterns have priority over generic
         * bank aliases.
         *
         * This is important for co-branded/fintech products.
         *
         * Example:
         * "Edge Federal Bank Credit Card"
         *
         * should resolve to:
         * Federal Bank
         *
         * and:
         *
         * "BOBCARD One Credit Card"
         *
         * should resolve to:
         * Bank of Baroda
         */
        val productMatch = findProductMatch(text)

        if (productMatch != null) {
            return productMatch
        }

        /*
         * If no product-specific configuration matches,
         * fall back to normal bank aliases.
         */
        return findBankMatch(text)
    }

    private fun findProductMatch(text: String): String? {

        /*
         * Sort by pattern length so a more specific pattern wins.
         *
         * Example:
         *
         * "bobcard one credit card"
         *
         * should win over:
         *
         * "bobcard"
         */
        val products = config.cardProducts
            .flatMap { product ->
                product.patterns.map { pattern ->
                    product to pattern
                }
            }
            .sortedByDescending {
                it.second.length
            }

        for ((product, pattern) in products) {

            if (text.contains(pattern.lowercase())) {
                return product.issuerBank
            }
        }

        return null
    }

    private fun findBankMatch(text: String): String? {

        /*
         * Again, prefer the longest alias.
         *
         * This avoids short aliases winning when a more
         * specific bank name is available.
         */
        val aliases = config.banks
            .flatMap { bank ->
                bank.aliases.map { alias ->
                    bank to alias
                }
            }
            .sortedByDescending {
                it.second.length
            }

        for ((bank, alias) in aliases) {

            if (text.contains(alias.lowercase())) {
                return bank.canonicalName
            }
        }

        return null
    }

    private fun normalize(sms: String): String {
        return sms
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")
    }
}