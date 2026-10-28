package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.data.model.QuestionType
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPreviewScreen(
    exam: Exam?,
    onBack: () -> Unit,
    onPrintPdf: (examToPrint: Exam, isModelAnswer: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    if (exam == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Active Preview Mode: Group A, Group B, or Model Answer Key
    var activeTab by remember { mutableStateOf("A") } // "A", "B", "MODEL"

    val displayedExam = remember(activeTab, exam) {
        when (activeTab) {
            "B" -> exam.createGroupB()
            else -> exam
        }
    }

    val isModelAnswerView = activeTab == "MODEL"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "معاينة ورقة الامتحان (A4)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isModelAnswerView) "نسخة الإجابة النموذجية وسلّم الدرجات" else "التنسيق المدرسي العراقي النهائي",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("preview_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { onPrintPdf(displayedExam, isModelAnswerView) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("print_export_pdf_button")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("طباعة / تصدير PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onPrintPdf(displayedExam, isModelAnswerView) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bottom_share_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة أو طباعة PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Switcher (نموذج أ / نموذج ب / الإجابة النموذجية)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = activeTab == "A",
                    onClick = { activeTab = "A" },
                    label = { Text("نموذج (أ) الأساسي", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = activeTab == "B",
                    onClick = { activeTab = "B" },
                    label = { Text("نموذج (ب) لمنع الغش", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = activeTab == "MODEL",
                    onClick = { activeTab = "MODEL" },
                    label = { Text("الإجابة النموذجية", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1.1f)
                )
            }

            // The Realistic A4 Sheet Canvas View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .border(0.8.dp, Color.DarkGray, RoundedCornerShape(2.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        // 1. Iraqi Top Header Block
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Right Side Info
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                                Text(displayedExam.header.ministry, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                Text(displayedExam.header.directorate, fontSize = 10.sp, color = Color.Black)
                                Text("مدرسة: ${displayedExam.header.schoolName}", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }

                            // Center Info
                            Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = if (isModelAnswerView) "الإجابة النموذجية وسلّم الدرجات" else displayedExam.header.examTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )
                                Text("للعام الدراسي: ${displayedExam.header.academicYear}", fontSize = 10.sp, color = Color.Black)
                                val groupTag = if (activeTab == "B") "[ نموذج (ب) ]" else "[ نموذج (أ) ]"
                                Text(groupTag, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryNavy)
                            }

                            // Left Side Info
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("المادة: ${displayedExam.header.subject}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                Text("الصف: ${displayedExam.header.grade}", fontSize = 10.sp, color = Color.Black)
                                Text("التاريخ: ${displayedExam.header.examDate}", fontSize = 10.sp, color = Color.Black)
                                Text("الزمن: ${displayedExam.header.duration}", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Student Info Row if enabled and not answer key
                        if (displayedExam.header.showStudentInfoRow && !isModelAnswerView) {
                            Text(
                                text = "اسم الطالب: ..............................................................   الرقم الامتحاني: ........................   القاعة: .............",
                                fontSize = 10.sp,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(thickness = 0.8.dp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // General Instruction Note
                        if (displayedExam.header.generalInstruction.isNotBlank()) {
                            Text(
                                text = displayedExam.header.generalInstruction,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(thickness = 0.8.dp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // 2. Questions List
                        displayedExam.sections.forEachIndexed { secIdx, section ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Section Title
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${section.title} (${section.sectionScore.toInt()} درجة)  ${section.instruction}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (section.note.isNotBlank()) {
                                    Text(
                                        text = "[${section.note}]",
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // SubQuestions
                                section.subQuestions.forEach { sub ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp, horizontal = 8.dp)
                                    ) {
                                        val scoreText = if (sub.score > 0) " (${sub.score.toInt()} درجات)" else ""
                                        Text(
                                            text = "${sub.label} ${sub.text}$scoreText",
                                            fontSize = 10.5.sp,
                                            color = Color.Black,
                                            textAlign = if (section.isLtr || sub.isLtr) TextAlign.Left else TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // MCQ options preview
                                        if (sub.options.isNotEmpty()) {
                                            val optionsLine = "( " + sub.options.mapIndexed { i, opt ->
                                                val l = listOf("أ", "ب", "ج", "د", "هـ").getOrElse(i) { "$i" }
                                                "$l- $opt"
                                            }.joinToString("  -  ") + " )"
                                            Text(
                                                text = optionsLine,
                                                fontSize = 10.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                                            )
                                        }

                                        // Matching Pairs preview
                                        if (sub.matchingPairs.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                                            ) {
                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                    Text("العمود (أ)", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
                                                    Text("العمود (ب)", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
                                                }
                                                sub.matchingPairs.forEach { pair ->
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text(pair.columnA, fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
                                                        Text(pair.columnB, fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        }

                                        // Drawing Box preview
                                        if (section.type == QuestionType.DRAWING || sub.drawingHeightDp > 0 && section.type == QuestionType.DRAWING) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((sub.drawingHeightDp * 0.4).dp)
                                                    .padding(vertical = 4.dp)
                                                    .border(0.8.dp, Color.Gray, RoundedCornerShape(2.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("[مساحة مخصصة للرسم والتأشير]", fontSize = 9.sp, color = Color.Gray)
                                            }
                                        }

                                        // Model Answer Section (if active)
                                        if (isModelAnswerView && sub.modelAnswer.isNotBlank()) {
                                            Surface(
                                                color = Color(0xFFF1F8E9),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 2.dp)
                                            ) {
                                                Text(
                                                    text = "← الإجابة النموذجية: ${sub.modelAnswer}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF1B5E20),
                                                    modifier = Modifier.padding(6.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(thickness = 0.6.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }

                        // 3. Iraqi Official Exam Footer
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Black)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مدرس المادة: ${displayedExam.header.teacherName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = Color.Black
                            )
                            Text(
                                text = displayedExam.header.closingWish,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "إدارة المدرسة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "- انتهت الأسئلة -",
                            textAlign = TextAlign.Center,
                            fontSize = 9.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
