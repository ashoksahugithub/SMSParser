package com.example.smsparser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smsparser.repository.SmsRepository
import com.example.smsparser.ui.TransactionListScreen
import com.example.smsparser.ui.TransactionViewModel
import com.example.smsparser.ui.TransactionViewModelFactory
import com.example.smsparser.ui.theme.SMSParserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository
        val repository = SmsRepository(this)
        
        setContent {
            SMSParserTheme {
                // Correctly initialize ViewModel using the factory
                val viewModel: TransactionViewModel = viewModel(
                    factory = TransactionViewModelFactory(repository)
                )

                TransactionListScreen(viewModel = viewModel)
            }
        }
    }
}
