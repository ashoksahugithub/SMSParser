package com.example.smsparser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard(
    includedCount: Int,
    excludedCount: Int,
    totalSpent: Double,
    totalReceived: Double,
    topExclusions: List<Pair<String, Int>>
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Included / Excluded
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                SummaryCount(
                    label = "Included",
                    count = includedCount,
                    color = Color(0xFF388E3C)
                )

                SummaryCount(
                    label = "Excluded",
                    count = excludedCount,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // INR totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                SummaryAmount(
                    label = "INR Debit",
                    amount = totalSpent,
                    color = Color(0xFFD32F2F)
                )

                SummaryAmount(
                    label = "INR Credit/Refund",
                    amount = totalReceived,
                    color = Color(0xFF388E3C)
                )
            }

            if (topExclusions.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Top Exclusions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                topExclusions.forEach { (reason, count) ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = reason,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = count.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCount(
    label: String,
    count: Int,
    color: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun SummaryAmount(
    label: String,
    amount: Double,
    color: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Text(
            text = "₹${"%.2f".format(amount)}",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}