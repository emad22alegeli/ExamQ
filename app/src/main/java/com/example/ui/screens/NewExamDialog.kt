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
import com.example.data.model.GradeStage
import com.example.data.model.SubjectType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewExamDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, subject: String, grade: String, targetScore: Double, examType: String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(SubjectType.SCIENCES.arabicName) }
    var selectedStage by remember { mutableStateOf(GradeStage.INTERMEDIATE) }
    var selectedGrade by remember { mutableStateOf("الثالث المتوسط") }
    var examType by remember { mutableStateOf("أسئلة امتحان نصف السنة") }
    var targetScore by remember { mutableDoubleStateOf(100.0) }
    var customTitle by remember { mutableStateOf("") }

    val gradeOptions = when (selectedStage) {
        GradeStage.PRIMARY -> listOf("الأول الابتدائي", "الثاني الابتدائي", "الثالث الابتدائي", "الرابع الابتدائي", "الخامس الابتدائي", "السادس الابتدائي (وزاري)")
        GradeStage.INTERMEDIATE -> listOf("الأول المتوسط", "الثاني المتوسط", "الثالث المتوسط (وزاري)")
        GradeStage.PREPARATORY -> listOf("الرابع العلمي", "الرابع الأدبي", "الخامس العلمي", "الخامس الأدبي", "السادس العلمي (وزاري)", "السادس الأدبي (وزاري)")
    }

    LaunchedEffect(selectedStage) {
        selectedGrade = gradeOptions.first()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إنشاء ورقة امتحان جديدة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Subject Selector Chips
                Text("اختر المادة الدراسية:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SubjectType.entries.forEach { subj ->
                        FilterChip(
                            selected = selectedSubject == subj.arabicName,
                            onClick = { selectedSubject = subj.arabicName },
                            label = { Text(subj.arabicName, fontSize = 11.5.sp) }
                        )
                    }
                }

                // 2. Educational Stage Selector
                Text("المرحلة الدراسية:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GradeStage.entries.forEach { stage ->
                        FilterChip(
                            selected = selectedStage == stage,
                            onClick = { selectedStage = stage },
                            label = { Text(stage.arabicName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. Grade Selection Chips
                Text("الصف الدراسي:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    gradeOptions.forEach { gr ->
                        FilterChip(
                            selected = selectedGrade == gr,
                            onClick = { selectedGrade = gr },
                            label = { Text(gr, fontSize = 11.sp) }
                        )
                    }
                }

                // 4. Exam Title Type
                Text("نوع الامتحان:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                val examTypes = listOf(
                    "أسئلة امتحان نصف السنة",
                    "أسئلة امتحان نهاية السنة (الدور الأول)",
                    "أسئلة امتحان الشهر الأول",
                    "أسئلة امتحان الشهر الثاني",
                    "أسئلة الامتحان التمهيدي"
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    examTypes.forEach { type ->
                        FilterChip(
                            selected = examType == type,
                            onClick = { examType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                // 5. Total Target Score (50 or 100)
                Text("الدرجة الكلية للامتحان:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = targetScore == 100.0,
                        onClick = { targetScore = 100.0 },
                        label = { Text("100 درجة (شامل)", fontWeight = FontWeight.Bold) }
                    )
                    FilterChip(
                        selected = targetScore == 50.0,
                        onClick = { targetScore = 50.0 },
                        label = { Text("50 درجة (شهري / شفهي)", fontWeight = FontWeight.Bold) }
                    )
                }

                // 6. Custom Title override
                OutlinedTextField(
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    label = { Text("عنوان مخصص (اختياري)") },
                    placeholder = { Text("مثال: امتحان $selectedSubject - $selectedGrade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = customTitle.ifBlank { "امتحان $selectedSubject - $selectedGrade ($examType)" }
                    onCreate(finalTitle, selectedSubject, selectedGrade, targetScore, examType)
                },
                modifier = Modifier.testTag("confirm_create_exam_button")
            ) {
                Text("بدء إنشاء الامتحان")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
