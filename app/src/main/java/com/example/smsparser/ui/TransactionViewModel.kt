package com.example.smsparser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smsparser.model.Decision
import com.example.smsparser.model.ParsedResult
import com.example.smsparser.model.TransactionType
import com.example.smsparser.repository.SmsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: SmsRepository
) : ViewModel() {

    val results: StateFlow<List<ParsedResult>> =
        repository.results

    val includedCount: StateFlow<Int> =
        results
            .map { list ->
                list.count {
                    it.decision == Decision.INCLUDE
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    val excludedCount: StateFlow<Int> =
        results
            .map { list ->
                list.count {
                    it.decision == Decision.EXCLUDE
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    /**
     * Requirement says INR debit amount.
     */
    val totalSpent: StateFlow<Double> =
        results
            .map { list ->
                list
                    .filter {
                        it.decision == Decision.INCLUDE &&
                                it.transaction?.currency == "INR" &&
                                it.transaction.type == TransactionType.DEBIT
                    }
                    .sumOf {
                        it.transaction?.amount ?: 0.0
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0.0
            )

    /**
     * Requirement says INR credit/refund amount.
     */
    val totalReceived: StateFlow<Double> =
        results
            .map { list ->
                list
                    .filter {
                        it.decision == Decision.INCLUDE &&
                                it.transaction?.currency == "INR" &&
                                (
                                        it.transaction.type == TransactionType.CREDIT ||
                                                it.transaction.type == TransactionType.REFUND
                                        )
                    }
                    .sumOf {
                        it.transaction?.amount ?: 0.0
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0.0
            )

    /**
     * Top exclusion reasons with their counts.
     *
     * Example:
     * UPI_BANK_ACCOUNT: 3
     * OTP: 2
     * BILL_DUE: 1
     */
    val topExclusions: StateFlow<List<Pair<String, Int>>> =
        results
            .map { list ->
                list
                    .filter {
                        it.decision == Decision.EXCLUDE
                    }
                    .groupingBy {
                        it.excludeReason?.name ?: "UNKNOWN"
                    }
                    .eachCount()
                    .toList()
                    .sortedByDescending {
                        it.second
                    }
                    .take(3)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.loadSamples()
        }
    }
}