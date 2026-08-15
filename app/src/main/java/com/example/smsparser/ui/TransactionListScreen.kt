package com.example.smsparser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smsparser.model.ParsedResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel
) {

    val results by viewModel.results.collectAsStateWithLifecycle()
    val includedCount by viewModel.includedCount.collectAsStateWithLifecycle()
    val excludedCount by viewModel.excludedCount.collectAsStateWithLifecycle()
    val totalSpent by viewModel.totalSpent.collectAsStateWithLifecycle()
    val totalReceived by viewModel.totalReceived.collectAsStateWithLifecycle()
    val topExclusions by viewModel.topExclusions.collectAsStateWithLifecycle()

    var selectedResult by remember {
        mutableStateOf<ParsedResult?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bank SMS Parser",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {

            SummaryCard(
                includedCount = includedCount,
                excludedCount = excludedCount,
                totalSpent = totalSpent,
                totalReceived = totalReceived,
                topExclusions = topExclusions
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {

                items(
                    items = results,
                    key = { it.id }
                ) { result ->

                    SmsResultItem(
                        result = result,
                        onClick = {
                            selectedResult = result
                        }
                    )
                }
            }
        }
    }

    selectedResult?.let { result ->

        SmsDetailScreen(
            result = result,
            onBack = {
                selectedResult = null
            }
        )
    }
}