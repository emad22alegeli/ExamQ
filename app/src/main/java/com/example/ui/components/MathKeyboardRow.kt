package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecondaryGoldLight

@Composable
fun MathKeyboardRow(
    onInsertSymbol: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mathSymbols = listOf(
        "²", "³", "⁴", "ⁿ", "₁", "₂", "₃",
        "√", "∛", "±", "×", "÷", "≠", "≤", "≥", "≈",
        "π", "θ", "α", "β", "λ", "Δ", "∑", "∫", "∞",
        "½", "⅓", "¼", "¾",
        "→", "⇌", "↑", "↓",
        "Ω", "µ", "°C", "H₂O", "CO₂", "NaCl"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = "رموز ومعادلات سريعة (رياضيات وفيزياء وكيمياء):",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            mathSymbols.forEach { sym ->
                FilledTonalButton(
                    onClick = { onInsertSymbol(sym) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("math_sym_$sym")
                ) {
                    Text(
                        text = sym,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
