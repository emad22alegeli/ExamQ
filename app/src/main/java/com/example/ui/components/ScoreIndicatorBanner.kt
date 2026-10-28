package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun ScoreIndicatorBanner(
    currentScore: Double,
    targetScore: Double,
    onAdjustTarget: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExact = kotlin.math.abs(currentScore - targetScore) < 0.01
    val difference = currentScore - targetScore

    val bannerBg = when {
        isExact -> SuccessGreen.copy(alpha = 0.12f)
        difference > 0 -> ErrorRed.copy(alpha = 0.12f)
        else -> WarningOrange.copy(alpha = 0.12f)
    }

    val statusColor = when {
        isExact -> SuccessGreen
        difference > 0 -> ErrorRed
        else -> WarningOrange
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bannerBg),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .testTag("score_indicator_banner")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExact) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مجموع الدرجات المحسوب: ${currentScore.toInt()} / ${targetScore.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Target score quick chips (50 / 100)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(
                        onClick = { onAdjustTarget(50.0) },
                        label = { Text("50 درجة", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (targetScore == 50.0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )
                    AssistChip(
                        onClick = { onAdjustTarget(100.0) },
                        label = { Text("100 درجة", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (targetScore == 100.0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Linear Progress Bar
            val progress = (currentScore / targetScore).coerceIn(0.0, 1.0).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.2f),
            )

            AnimatedVisibility(visible = !isExact) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val alertText = if (difference < 0) {
                        "⚠️ تنبيه: ينقصك ${kotlin.math.abs(difference).toInt()} درجة ليصل مجموع الأسئلة إلى الدرجة الكلية (${targetScore.toInt()})."
                    } else {
                        "⚠️ تنبيه: المجموع يتجاوز الدرجة الكلية بـ ${difference.toInt()} درجة. يمكنك تعديل درجات الفقرات أو جعل أحد الأسئلة إضافياً (ترك)."
                    }
                    Text(
                        text = alertText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
