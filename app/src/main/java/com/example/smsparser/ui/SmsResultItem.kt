package com.example.smsparser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smsparser.model.Decision
import com.example.smsparser.model.ParsedResult
import com.example.smsparser.model.TransactionType

@Composable
fun SmsResultItem(
    result: ParsedResult,
    onClick: () -> Unit
) {
    val isIncluded = result.decision == Decision.INCLUDE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick)
            .alpha(if (isIncluded) 1f else 0.75f),
        colors = CardDefaults.cardColors(
            containerColor = if (isIncluded) {
                Color.White
            } else {
                Color(0xFFF1F1F1)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            if (isIncluded) {
                IncludedRow(result)
            } else {
                ExcludedRow(result)
            }

            Spacer(
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = "Confidence: ${(result.confidence * 100).toInt()}%",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun IncludedRow(
    result: ParsedResult
) {

    val transaction = result.transaction ?: return

    val isDebit = transaction.type == TransactionType.DEBIT

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        BankInitial(
            bankName = transaction.bankName
        )

        Spacer(
            modifier = Modifier.size(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = transaction.merchantName
                    ?: transaction.bankName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.size(3.dp)
            )

            Text(
                text = buildString {

                    transaction.date?.let {
                        append(it)
                    }

                    transaction.bankName.let {
                        append(" • ")
                        append(it)
                    }
                },
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.size(3.dp)
            )

            Text(
                text = transaction.type.name,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Text(
            text = buildString {
                append(if (isDebit) "- " else "+ ")
                append(transaction.currency)
                append(" ")
                append("%.2f".format(transaction.amount))
            },
            color = if (isDebit) {
                Color(0xFFD32F2F)
            } else {
                Color(0xFF388E3C)
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ExcludedRow(
    result: ParsedResult
) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = result.excludeReason?.name ?: "UNKNOWN",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Text(
                text = "EXCLUDED",
                modifier = Modifier
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        Text(
            text = result.rawSms,
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.size(4.dp)
        )

        Text(
            text = "Reason: ${result.excludeReason?.name ?: "UNKNOWN"}",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun BankInitial(
    bankName: String?
) {

    val initials = bankName
        ?.trim()
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") {
            it.first().uppercase()
        }
        ?: "?"

    Text(
        text = initials,
        modifier = Modifier
            .size(42.dp)
            .background(
                color = Color(0xFFE8EAF6),
                shape = CircleShape
            )
            .padding(10.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray
    )
}