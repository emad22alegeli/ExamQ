package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.IraqiExamMakerTheme
import com.example.ui.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IraqiExamMakerTheme {
                // Iraqi School Exams and entire application interface is RTL Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        ExamMakerApp(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun ExamMakerApp(
    viewModel: ExamViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val questionBank by viewModel.questionBank.collectAsStateWithLifecycle()
    val currentExam by viewModel.currentExam.collectAsStateWithLifecycle()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsStateWithLifecycle()
    val defaultHeader by viewModel.defaultHeader.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showNewExamDialog by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = currentScreen,
        label = "screen_transition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            is Screen.Home -> {
                HomeScreen(
                    exams = exams,
                    onCreateNewExam = { showNewExamDialog = true },
                    onOpenExam = { examId ->
                        viewModel.loadExam(examId)
                        currentScreen = Screen.ExamEditor(examId)
                    },
                    onPreviewExam = { examId ->
                        viewModel.loadExam(examId)
                        currentScreen = Screen.ExamPreview(examId)
                    },
                    onDuplicateExam = { exam ->
                        viewModel.duplicateExam(exam)
                    },
                    onDeleteExam = { exam ->
                        viewModel.deleteExam(exam)
                    },
                    onOpenQuestionBank = {
                        currentScreen = Screen.QuestionBank
                    },
                    onOpenSettings = {
                        currentScreen = Screen.Settings
                    }
                )
            }

            is Screen.ExamEditor -> {
                ExamEditorScreen(
                    exam = currentExam,
                    isGeneratingAi = isGeneratingAi,
                    onBack = { currentScreen = Screen.Home },
                    onPreview = {
                        currentExam?.let {
                            currentScreen = Screen.ExamPreview(it.id)
                        }
                    },
                    onUpdateExam = { updated ->
                        viewModel.updateCurrentExam(updated)
                    },
                    onUpdateHeader = { header ->
                        viewModel.updateHeader(header)
                    },
                    onUpdateTargetScore = { score ->
                        viewModel.updateTargetScore(score)
                    },
                    onAddSection = { type ->
                        viewModel.addSection(type)
                    },
                    onReorderSection = { from, to ->
                        viewModel.reorderSection(from, to)
                    },
                    onDuplicateSection = { index ->
                        viewModel.duplicateSection(index)
                    },
                    onDeleteSection = { index ->
                        viewModel.deleteSection(index)
                    },
                    onSaveToQuestionBank = { sub, type ->
                        viewModel.saveQuestionToBank(sub, type)
                    },
                    onGenerateAi = { topic ->
                        viewModel.generateAiQuestions(topic)
                    }
                )
            }

            is Screen.ExamPreview -> {
                ExamPreviewScreen(
                    exam = currentExam,
                    onBack = {
                        currentExam?.let {
                            currentScreen = Screen.ExamEditor(it.id)
                        } ?: run {
                            currentScreen = Screen.Home
                        }
                    },
                    onPrintPdf = { examToPrint, isModelAnswer ->
                        viewModel.generateAndSharePdf(examToPrint, isModelAnswer)
                    }
                )
            }

            is Screen.QuestionBank -> {
                QuestionBankScreen(
                    questions = questionBank,
                    hasActiveExam = currentExam != null,
                    onBack = { currentScreen = Screen.Home },
                    onImportToExam = { item ->
                        viewModel.importQuestionFromBankToExam(item)
                    },
                    onDeleteQuestion = { id ->
                        viewModel.deleteQuestionFromBank(id)
                    },
                    onAddNewQuestion = { newItem ->
                        viewModel.addQuestionBankItem(newItem)
                    }
                )
            }

            is Screen.Settings -> {
                SettingsScreen(
                    currentHeader = defaultHeader,
                    onBack = { currentScreen = Screen.Home },
                    onSaveProfile = { ministry, directorate, schoolName, teacherName, academicYear, closingWish ->
                        viewModel.saveProfileSettings(ministry, directorate, schoolName, teacherName, academicYear, closingWish)
                    },
                    onExportBackup = { onJsonReady ->
                        viewModel.exportBackup(onJsonReady)
                    },
                    onImportBackup = { json ->
                        viewModel.importBackup(json)
                    }
                )
            }
        }
    }

    if (showNewExamDialog) {
        NewExamDialog(
            onDismiss = { showNewExamDialog = false },
            onCreate = { title, subject, grade, targetScore, examType ->
                val newId = viewModel.createNewExam(
                    title = title,
                    subject = subject,
                    grade = grade,
                    targetScore = targetScore,
                    examTitleType = examType
                )
                showNewExamDialog = false
                currentScreen = Screen.ExamEditor(newId)
            }
        )
    }
}
