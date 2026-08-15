package com.example.smsparser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smsparser.repository.SmsRepository

class TransactionViewModelFactory(private val repository: SmsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
