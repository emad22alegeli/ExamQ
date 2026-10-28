package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamHeader
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold

@Composable
fun IraqiExamHeaderCard(
    header: ExamHeader,
    onUpdateHeader: (ExamHeader) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .testTag("iraqi_header_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Top Bar with Edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ترويسة ورقة الامتحان الرسمية (وزارية / مدرسية)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                FilledTonalButton(
                    onClick = { showEditDialog = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("edit_header_button")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل الترويسة", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تعديل الترويسة", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Authentic 3-Column Iraqi Header Layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Right Column (الجمهورية، الإدارة، المدرسة)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = header.ministry,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = header.directorate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "مدرسة: ${header.schoolName}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Center Column ("بسمه تعالى" وعنوان الامتحان)
                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = header.examTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "للعام الدراسي: ${header.academicYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Group Chip
                        SuggestionChip(
                            onClick = {
                                val nextGroup = if (header.examGroup == "A") "B" else "A"
                                onUpdateHeader(header.copy(examGroup = nextGroup))
                            },
                            label = {
                                Text(
                                    text = if (header.examGroup == "B") "نموذج (ب)" else "نموذج (أ)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (header.examGroup == "B") SecondaryGold.copy(alpha = 0.2f) else PrimaryNavy.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }

                    // Left Column (المادة، الصف، التاريخ، الزمن)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "المادة: ${header.subject}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الصف: ${header.grade}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "التاريخ: ${header.examDate}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "الزمن: ${header.duration}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // General instruction note
            if (header.generalInstruction.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = header.generalInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showEditDialog) {
        EditHeaderDialog(
            header = header,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onUpdateHeader(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditHeaderDialog(
    header: ExamHeader,
    onDismiss: () -> Unit,
    onSave: (ExamHeader) -> Unit
) {
    var ministry by remember { mutableStateOf(header.ministry) }
    var directorate by remember { mutableStateOf(header.directorate) }
    var schoolName by remember { mutableStateOf(header.schoolName) }
    var examTitle by remember { mutableStateOf(header.examTitle) }
    var academicYear by remember { mutableStateOf(header.academicYear) }
    var subject by remember { mutableStateOf(header.subject) }
    var grade by remember { mutableStateOf(header.grade) }
    var examDate by remember { mutableStateOf(header.examDate) }
    var duration by remember { mutableStateOf(header.duration) }
    var generalInstruction by remember { mutableStateOf(header.generalInstruction) }
    var teacherName by remember { mutableStateOf(header.teacherName) }
    var closingWish by remember { mutableStateOf(header.closingWish) }
    var showStudentInfoRow by remember { mutableStateOf(header.showStudentInfoRow) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل ترويسة ورقة الامتحان", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("اسم المدرسة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = directorate,
                    onValueChange = { directorate = it },
                    label = { Text("المديرية العامة للتربية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("المادة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = grade,
                        onValueChange = { grade = it },
                        label = { Text("الصف / المرحلة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = examDate,
                        onValueChange = { examDate = it },
                        label = { Text("التاريخ") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("الزمن") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = examTitle,
                    onValueChange = { examTitle = it },
                    label = { Text("عنوان الامتحان (نصف السنة / الشهر الأول...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = generalInstruction,
                    onValueChange = { generalInstruction = it },
                    label = { Text("ملاحظة عامة (أجب عن 5 أسئلة...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("اسم مدرس المادة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = { academicYear = it },
                        label = { Text("العام الدراسي") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = showStudentInfoRow,
                        onCheckedChange = { showStudentInfoRow = it }
                    )
                    Text("إظهار سطر اسم الطالب ورقمه الامتحاني", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        header.copy(
                            ministry = ministry,
                            directorate = directorate,
                            schoolName = schoolName,
                            examTitle = examTitle,
                            academicYear = academicYear,
                            subject = subject,
                            grade = grade,
                            examDate = examDate,
                            duration = duration,
                            generalInstruction = generalInstruction,
                            teacherName = teacherName,
                            closingWish = closingWish,
                            showStudentInfoRow = showStudentInfoRow
                        )
                    )
                }
            ) {
                Text("حفظ الترويسة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
