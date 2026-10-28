package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecondaryGold

@Composable
fun AiQuestionGeneratorDialog(
    currentSubject: String,
    currentGrade: String,
    isGenerating: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (topicText: String) -> Unit
) {
    var topicText by remember { mutableStateOf("") }

    val sampleTopics = when (currentSubject) {
        "الفيزياء" -> listOf("الكهربائية الساكنة وقانون كولوم", "المغناطيسية والمجال المغناطيسي", "القدرة والطاقة الكهربائية")
        "الكيمياء" -> listOf("التركيب الذري والجدول الدوري", "المعادلات الكيميائية وموازنتها", "المحاليل والتعبير عن التركيز")
        "الرياضيات" -> listOf("حل المعادلات من الدرجة الثانية", "المتتابعات الحسابية والهندسية", "الهندسة الإحداثية والميل")
        "الأحياء" -> listOf("الجهاز الهيكلي والعظمي", "الجهاز الدوري وضغط الدم", "الوراثة والتكاثر الخلوي")
        "اللغة العربية" -> listOf("أسلوب الاستفهام والنفي", "الممنوع من الصرف", "أدب العصر العباسي وشعر السياب")
        "اللغة الإنجليزية" -> listOf("Past Simple vs Continuous", "Passive Voice & Conditionals", "Descriptive Writing & Vocabulary")
        else -> listOf("المفاهيم العامة للوحدة الأولى", "موضوعات الامتحان النهائي", "تعاريف وأسئلة علل")
    }

    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SecondaryGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("توليد أسئلة ذكي (AI)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "سيقوم المولد الذكي بصياغة أسئلة امتحانية متوافقة مع المنهج العراقي لمادة ($currentSubject) لصف ($currentGrade) مع نموذج الإجابة وسلّم الدرجات.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    label = { Text("أدخل عنوان الفصل، الموضوع، أو ألصق نصاً") },
                    placeholder = { Text("مثال: الفصل الثاني - المغناطيسية وخطوط المجال...") },
                    minLines = 3,
                    maxLines = 6,
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "أو اختر موضوعاً مقترحاً بنقرة سريعة:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleTopics.take(2).forEach { sample ->
                        AssistChip(
                            onClick = { topicText = sample },
                            label = { Text(sample, fontSize = 11.sp) },
                            enabled = !isGenerating
                        )
                    }
                }

                if (isGenerating) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("جارٍ صياغة الأسئلة ونموذج الإجابة...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(topicText) },
                enabled = !isGenerating,
                modifier = Modifier.testTag("ai_generate_confirm_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("توليد وإضافة للامتحان")
            }
        },
        dismissButton = {
            if (!isGenerating) {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )
}
