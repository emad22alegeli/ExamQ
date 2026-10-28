package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold

@Composable
fun QuestionSectionCard(
    section: ExamSection,
    sectionIndex: Int,
    totalSections: Int,
    onUpdateSection: (ExamSection) -> Unit,
    onDeleteSection: () -> Unit,
    onDuplicateSection: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onSaveToQuestionBank: (SubQuestion, QuestionType) -> Unit,
    onInsertSymbolTarget: (focusSubId: String?, symbol: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    var showTypeMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .testTag("section_card_$sectionIndex")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Section Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Question Number Badge (e.g. س1)
                    Surface(
                        color = PrimaryNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = section.title.take(4),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Question Type Selector Pill
                    Box {
                        AssistChip(
                            onClick = { showTypeMenu = true },
                            label = { Text(section.type.titleAr, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )

                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false }
                        ) {
                            QuestionType.entries.forEach { qType ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(qType.titleAr, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(qType.categoryAr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        onUpdateSection(section.copy(type = qType))
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Controls: Move up/down, duplicate, delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = sectionIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "تحريك لأعلى", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = sectionIndex < totalSections - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "تحريك لأسفل", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDuplicateSection,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "تكرار السؤال", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDeleteSection,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف السؤال", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section Title and Score Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = section.title,
                    onValueChange = { onUpdateSection(section.copy(title = it)) },
                    label = { Text("عنوان السؤال (س1 / س2)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = section.sectionScore.toString().replace(".0", ""),
                    onValueChange = {
                        val score = it.toDoubleOrNull() ?: section.sectionScore
                        onUpdateSection(section.copy(sectionScore = score))
                    },
                    label = { Text("درجة السؤال") },
                    singleLine = true,
                    modifier = Modifier.width(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Instruction field e.g. "أجب عن أربعة أفرع فقط:"
            OutlinedTextField(
                value = section.instruction,
                onValueChange = { onUpdateSection(section.copy(instruction = it)) },
                label = { Text("صيغة الطلب أو التوجيه (مثال: أجب عما يأتي / اختر ما بين الأقواس)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Optional note field
            OutlinedTextField(
                value = section.note,
                onValueChange = { onUpdateSection(section.copy(note = it)) },
                label = { Text("ملاحظة فرعية (اختياري: لكل فرع 5 درجات / أجب عن اثنين فقط)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subquestions header with Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الفقرات والفروع (${section.subQuestions.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = {
                        val nextIndex = section.subQuestions.size + 1
                        val nextLabel = "${nextIndex}-"
                        val defaultScore = if (section.subQuestions.isNotEmpty()) {
                            section.subQuestions.first().score
                        } else 5.0

                        val newSub = SubQuestion(
                            id = java.util.UUID.randomUUID().toString(),
                            label = nextLabel,
                            text = "",
                            score = defaultScore,
                            options = if (section.type == QuestionType.MULTIPLE_CHOICE) listOf("الخيار الأول", "الخيار الثاني", "الخيار الثالث") else emptyList(),
                            matchingPairs = if (section.type == QuestionType.MATCHING) listOf(MatchingPair(columnA = "عنصر 1", columnB = "مطابق 1")) else emptyList()
                        )
                        onUpdateSection(section.copy(subQuestions = section.subQuestions + newSub))
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة فرع / فقرة", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of SubQuestions
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                section.subQuestions.forEachIndexed { subIndex, sub ->
                    SubQuestionItemView(
                        sub = sub,
                        subIndex = subIndex,
                        sectionType = section.type,
                        onUpdate = { updatedSub ->
                            val updatedList = section.subQuestions.toMutableList()
                            updatedList[subIndex] = updatedSub
                            onUpdateSection(section.copy(subQuestions = updatedList))
                        },
                        onDelete = {
                            val updatedList = section.subQuestions.toMutableList()
                            updatedList.removeAt(subIndex)
                            onUpdateSection(section.copy(subQuestions = updatedList))
                        },
                        onSaveToBank = { onSaveToQuestionBank(sub, section.type) }
                    )
                }
            }
        }
    }
}

@Composable
fun SubQuestionItemView(
    sub: SubQuestion,
    subIndex: Int,
    sectionType: QuestionType,
    onUpdate: (SubQuestion) -> Unit,
    onDelete: () -> Unit,
    onSaveToBank: () -> Unit
) {
    var showModelAnswer by remember { mutableStateOf(sub.modelAnswer.isNotBlank()) }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Label, Text and Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sub.label,
                    onValueChange = { onUpdate(sub.copy(label = it)) },
                    label = { Text("الترقيم") },
                    singleLine = true,
                    modifier = Modifier.width(70.dp)
                )

                OutlinedTextField(
                    value = sub.text,
                    onValueChange = { onUpdate(sub.copy(text = it)) },
                    label = { Text("نص الفقرة / السؤال") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = sub.score.toString().replace(".0", ""),
                    onValueChange = {
                        val score = it.toDoubleOrNull() ?: sub.score
                        onUpdate(sub.copy(score = score))
                    },
                    label = { Text("الدرجة") },
                    singleLine = true,
                    modifier = Modifier.width(65.dp)
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "حذف الفقرة", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }

            // MCQ Options Editor
            if (sectionType == QuestionType.MULTIPLE_CHOICE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "الخيارات (بين أقواس تفصل بينها شرطات):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sub.options.forEachIndexed { optIdx, opt ->
                        AssistChip(
                            onClick = {},
                            label = { Text(opt, fontSize = 12.sp) },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val newOpts = sub.options.toMutableList()
                                        newOpts.removeAt(optIdx)
                                        onUpdate(sub.copy(options = newOpts))
                                    },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                    }
                }
                var newOptionText by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = newOptionText,
                        onValueChange = { newOptionText = it },
                        placeholder = { Text("أدخل خياراً إضافياً...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (newOptionText.isNotBlank()) {
                                onUpdate(sub.copy(options = sub.options + newOptionText.trim()))
                                newOptionText = ""
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إضافة خيار")
                    }
                }
            }

            // Matching Pairs Editor
            if (sectionType == QuestionType.MATCHING) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("الأزواج للمطابقة بين العمودين (أ) و(ب):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                sub.matchingPairs.forEachIndexed { pairIdx, pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pair.columnA,
                            onValueChange = {
                                val updatedPairs = sub.matchingPairs.toMutableList()
                                updatedPairs[pairIdx] = pair.copy(columnA = it)
                                onUpdate(sub.copy(matchingPairs = updatedPairs))
                            },
                            placeholder = { Text("العمود أ") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pair.columnB,
                            onValueChange = {
                                val updatedPairs = sub.matchingPairs.toMutableList()
                                updatedPairs[pairIdx] = pair.copy(columnB = it)
                                onUpdate(sub.copy(matchingPairs = updatedPairs))
                            },
                            placeholder = { Text("العمود ب") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val updatedPairs = sub.matchingPairs.toMutableList()
                                updatedPairs.removeAt(pairIdx)
                                onUpdate(sub.copy(matchingPairs = updatedPairs))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                TextButton(
                    onClick = {
                        val updatedPairs = sub.matchingPairs + MatchingPair(columnA = "", columnB = "")
                        onUpdate(sub.copy(matchingPairs = updatedPairs))
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة زوج مطابقة")
                }
            }

            // Drawing space height configuration
            if (sectionType == QuestionType.DRAWING) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ارتفاع مساحة الرسم في الورقة:", style = MaterialTheme.typography.bodySmall)
                    listOf(
                        "صغير" to 100,
                        "متوسط" to 150,
                        "كبير" to 220
                    ).forEach { (name, h) ->
                        FilterChip(
                            selected = sub.drawingHeightDp == h,
                            onClick = { onUpdate(sub.copy(drawingHeightDp = h)) },
                            label = { Text(name, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Bottom Actions: Toggle Model Answer, Save to Bank
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showModelAnswer = !showModelAnswer }
                ) {
                    Icon(
                        imageVector = if (showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showModelAnswer) "إخفاء الإجابة النموذجية" else "إضافة / تعديل الإجابة النموذجية", fontSize = 11.sp)
                }

                TextButton(
                    onClick = onSaveToBank
                ) {
                    Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حفظ في بنك الأسئلة", fontSize = 11.sp)
                }
            }

            AnimatedVisibility(visible = showModelAnswer) {
                OutlinedTextField(
                    value = sub.modelAnswer,
                    onValueChange = { onUpdate(sub.copy(modelAnswer = it)) },
                    label = { Text("الإجابة النموذجية وسلم توزيع الدرجة (خاص بالمعلم)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
}
