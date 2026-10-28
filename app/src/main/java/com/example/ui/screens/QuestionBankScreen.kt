package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.QuestionBankItem
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import com.example.data.model.SubjectType
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionBankScreen(
    questions: List<QuestionBankItem>,
    hasActiveExam: Boolean,
    onBack: () -> Unit,
    onImportToExam: (QuestionBankItem) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onAddNewQuestion: (QuestionBankItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("الكل") }
    var selectedDifficulty by remember { mutableStateOf("الكل") } // الكل, سهل, متوسط, صعب
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredQuestions = questions.filter { q ->
        val matchesSearch = q.subQuestion.text.contains(searchQuery, ignoreCase = true) ||
                q.chapter.contains(searchQuery, ignoreCase = true) ||
                q.subQuestion.modelAnswer.contains(searchQuery, ignoreCase = true)

        val matchesSubject = selectedSubject == "الكل" || q.subject == selectedSubject
        val matchesDifficulty = selectedDifficulty == "الكل" || q.difficulty == selectedDifficulty

        matchesSearch && matchesSubject && matchesDifficulty
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("بنك الأسئلة الشامل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("مكتبة أسئلة مصنفة حسب المادة والصف والصعوبة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("bank_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_to_bank_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "إضافة سؤال جديد", tint = PrimaryNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 40.dp,
                start = 14.dp,
                end = 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث في بنك الأسئلة...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Subject Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("المادة الدراسية:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("الكل").plus(SubjectType.entries.map { it.arabicName }).forEach { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Difficulty Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("مستوى الصعوبة:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    listOf("الكل", "سهل", "متوسط", "صعب").forEach { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Questions count
            item {
                Text(
                    text = "عدد الأسئلة المتاحة (${filteredQuestions.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (filteredQuestions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("لا توجد أسئلة مطابقة للبحث أو الفلتر المختار.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(filteredQuestions, key = { it.id }) { item ->
                    BankQuestionCard(
                        item = item,
                        canInsertIntoExam = hasActiveExam,
                        onInsert = { onImportToExam(item) },
                        onDelete = { onDeleteQuestion(item.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        CreateBankQuestionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                onAddNewQuestion(newItem)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BankQuestionCard(
    item: QuestionBankItem,
    canInsertIntoExam: Boolean,
    onInsert: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = PrimaryNavy.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.subject,
                            color = PrimaryNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.grade,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    val diffColor = when (item.difficulty) {
                        "صعب" -> Color(0xFFC62828)
                        "سهل" -> Color(0xFF2E7D32)
                        else -> Color(0xFFEF6C00)
                    }
                    Surface(
                        color = diffColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.difficulty,
                            color = diffColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Question Type & Chapter
            Text(
                text = "${item.type.titleAr} • ${item.chapter}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Text
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subQuestion.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            // Multiple choice options if exist
            if (item.subQuestion.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الخيارات: ( " + item.subQuestion.options.joinToString("  -  ") + " )",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Model Answer preview
            if (item.subQuestion.modelAnswer.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "الإجابة النموذجية: ${item.subQuestion.modelAnswer}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            // Insert Button
            if (canInsertIntoExam) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onInsert,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إدراج في الامتحان الحالي", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateBankQuestionDialog(
    onDismiss: () -> Unit,
    onSave: (QuestionBankItem) -> Unit
) {
    var subject by remember { mutableStateOf(SubjectType.SCIENCES.arabicName) }
    var grade by remember { mutableStateOf("الثالث المتوسط") }
    var chapter by remember { mutableStateOf("الفصل الأول") }
    var difficulty by remember { mutableStateOf("متوسط") }
    var questionType by remember { mutableStateOf(QuestionType.DEFINITIONS) }
    var questionText by remember { mutableStateOf("") }
    var modelAnswer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة سؤال جديد لبنك الأسئلة", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                        label = { Text("الصف") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { chapter = it },
                        label = { Text("الفصل / الوحدة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        label = { Text("الصعوبة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("نص السؤال أو المصطلح") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = modelAnswer,
                    onValueChange = { modelAnswer = it },
                    label = { Text("الإجابة النموذجية") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (questionText.isNotBlank()) {
                        val item = QuestionBankItem(
                            subject = subject,
                            grade = grade,
                            chapter = chapter,
                            difficulty = difficulty,
                            type = questionType,
                            subQuestion = SubQuestion(
                                text = questionText,
                                modelAnswer = modelAnswer,
                                score = 5.0
                            )
                        )
                        onSave(item)
                    }
                }
            ) {
                Text("حفظ في البنك")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
