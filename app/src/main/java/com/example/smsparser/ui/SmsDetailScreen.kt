package com.example.smsparser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smsparser.model.Decision
import com.example.smsparser.model.ParsedResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsDetailScreen(
    result: ParsedResult,
    onBack: () -> Unit
) {

    BackHandler(
        onBack = onBack
    )

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("SMS Details")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DetailField(
                label = "ID",
                value = result.id.toString()
            )

            DetailField(
                label = "Decision",
                value = result.decision.name,
                valueColor =
                    if (result.decision == Decision.INCLUDE) {
                        Color(0xFF388E3C)
                    } else {
                        Color(0xFFD32F2F)
                    }
            )

            DetailField(
                label = "Confidence",
                value = "${(result.confidence * 100).toInt()}%"
            )

            if (result.decision == Decision.EXCLUDE) {

                DetailField(
                    label = "Exclude Reason",
                    value = result.excludeReason?.name ?: "UNKNOWN"
                )

            } else {

                result.transaction?.let { transaction ->

                    DetailField(
                        label = "Type",
                        value = transaction.type.name
                    )

                    DetailField(
                        label = "Amount",
                        value = "${transaction.currency} ${
                            "%.2f".format(transaction.amount)
                        }"
                    )

                    DetailField(
                        label = "Bank",
                        value = transaction.bankName
                    )

                    DetailField(
                        label = "Card",
                        value = transaction.cardLastFour
                            ?.let { "Ending in $it" }
                            ?: "N/A"
                    )

                    DetailField(
                        label = "Merchant",
                        value = transaction.merchantName ?: "N/A"
                    )

                    DetailField(
                        label = "Date",
                        value = transaction.date ?: "N/A"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Raw SMS",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F3F4)
                )
            ) {

                Text(
                    text = result.rawSms,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}