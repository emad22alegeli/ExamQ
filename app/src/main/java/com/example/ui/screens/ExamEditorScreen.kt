package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.data.model.ExamHeader
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import com.example.ui.components.IraqiExamHeaderCard
import com.example.ui.components.MathKeyboardRow
import com.example.ui.components.QuestionSectionCard
import com.example.ui.components.ScoreIndicatorBanner
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamEditorScreen(
    exam: Exam?,
    isGeneratingAi: Boolean,
    onBack: () -> Unit,
    onPreview: () -> Unit,
    onUpdateExam: (Exam) -> Unit,
    onUpdateHeader: (ExamHeader) -> Unit,
    onUpdateTargetScore: (Double) -> Unit,
    onAddSection: (QuestionType) -> Unit,
    onReorderSection: (fromIndex: Int, toIndex: Int) -> Unit,
    onDuplicateSection: (Int) -> Unit,
    onDeleteSection: (Int) -> Unit,
    onSaveToQuestionBank: (SubQuestion, QuestionType) -> Unit,
    onGenerateAi: (topic: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    if (exam == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var showAddQuestionTypeDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showMathKeyboard by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = exam.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${exam.subject} • ${exam.grade}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("editor_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAiDialog = true },
                        modifier = Modifier.testTag("editor_ai_button")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "توليد ذكي", tint = SecondaryGold)
                    }

                    FilledTonalButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("editor_preview_button")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("معاينة A4", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddQuestionTypeDialog = true },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                text = { Text("إضافة سؤال جديد", fontWeight = FontWeight.Bold) },
                containerColor = PrimaryNavy,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_question_fab")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 90.dp,
                start = 14.dp,
                end = 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Iraqi Exam Header Card
            item {
                IraqiExamHeaderCard(
                    header = exam.header,
                    onUpdateHeader = onUpdateHeader
                )
            }

            // 2. Score Indicator Banner
            item {
                val currentCalculatedScore = exam.calculateTotalCalculatedScore()
                ScoreIndicatorBanner(
                    currentScore = currentCalculatedScore,
                    targetScore = exam.totalTargetScore,
                    onAdjustTarget = onUpdateTargetScore
                )
            }

            // 3. Quick Action Toolbar & Math Keyboard Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = showMathKeyboard,
                            onClick = { showMathKeyboard = !showMathKeyboard },
                            label = { Text("لوحة الرموز والكسور (√, ², π)", fontSize = 11.5.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }

                    // Model A / Model B Toggle
                    AssistChip(
                        onClick = {
                            val nextGroup = if (exam.header.examGroup == "A") "B" else "A"
                            onUpdateHeader(exam.header.copy(examGroup = nextGroup))
                        },
                        label = {
                            Text(
                                text = if (exam.header.examGroup == "B") "النموذج النشط: (ب)" else "النموذج النشط: (أ)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            // 4. Quick Math/Science Symbol Insert Strip
            if (showMathKeyboard) {
                item {
                    MathKeyboardRow(
                        onInsertSymbol = { sym ->
                            // Appends symbol to the last active section or last subquestion
                            if (exam.sections.isNotEmpty()) {
                                val lastSec = exam.sections.last()
                                if (lastSec.subQuestions.isNotEmpty()) {
                                    val lastSub = lastSec.subQuestions.last()
                                    val updatedSub = lastSub.copy(text = lastSub.text + sym)
                                    val updatedSubs = lastSec.subQuestions.toMutableList()
                                    updatedSubs[updatedSubs.lastIndex] = updatedSub
                                    val updatedSec = lastSec.copy(subQuestions = updatedSubs)
                                    val updatedSecs = exam.sections.toMutableList()
                                    updatedSecs[updatedSecs.lastIndex] = updatedSec
                                    onUpdateExam(exam.copy(sections = updatedSecs))
                                } else {
                                    val updatedSec = lastSec.copy(instruction = lastSec.instruction + sym)
                                    val updatedSecs = exam.sections.toMutableList()
                                    updatedSecs[updatedSecs.lastIndex] = updatedSec
                                    onUpdateExam(exam.copy(sections = updatedSecs))
                                }
                            }
                        }
                    )
                }
            }

            // 5. Sections Title & Add Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أسئلة الامتحان (${exam.sections.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { showAiDialog = true }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("توليد أسئلة بالذكاء الاصطناعي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 6. Question Section Cards
            itemsIndexed(exam.sections, key = { _, sec -> sec.id }) { index, section ->
                QuestionSectionCard(
                    section = section,
                    sectionIndex = index,
                    totalSections = exam.sections.size,
                    onUpdateSection = { updatedSection ->
                        val updatedList = exam.sections.toMutableList()
                        updatedList[index] = updatedSection
                        onUpdateExam(exam.copy(sections = updatedList))
                    },
                    onDeleteSection = { onDeleteSection(index) },
                    onDuplicateSection = { onDuplicateSection(index) },
                    onMoveUp = { onReorderSection(index, index - 1) },
                    onMoveDown = { onReorderSection(index, index + 1) },
                    onSaveToQuestionBank = onSaveToQuestionBank,
                    onInsertSymbolTarget = { _, _ -> }
                )
            }
        }
    }

    // Modal to choose one of the 13 question types
    if (showAddQuestionTypeDialog) {
        AddQuestionTypeDialog(
            onDismiss = { showAddQuestionTypeDialog = false },
            onSelectType = { selectedType ->
                onAddSection(selectedType)
                showAddQuestionTypeDialog = false
            }
        )
    }

    // AI Generation Dialog
    if (showAiDialog) {
        AiQuestionGeneratorDialog(
            currentSubject = exam.subject,
            currentGrade = exam.grade,
            isGenerating = isGeneratingAi,
            onDismiss = { showAiDialog = false },
            onGenerate = { topic ->
                onGenerateAi(topic)
                showAiDialog = false
            }
        )
    }
}

@Composable
fun AddQuestionTypeDialog(
    onDismiss: () -> Unit,
    onSelectType: (QuestionType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر نوع السؤال الجديد (13 نوعاً)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuestionType.entries.size) { idx ->
                    val qType = QuestionType.entries[idx]
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        onClick = { onSelectType(qType) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryNavy.copy(alpha = 0.12f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${idx + 1}", fontWeight = FontWeight.Bold, color = PrimaryNavy, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(qType.titleAr, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(qType.categoryAr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
