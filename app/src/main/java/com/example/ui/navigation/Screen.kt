package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data class ExamEditor(val examId: Long) : Screen("exam_editor/$examId") {
        companion object {
            const val ROUTE = "exam_editor/{examId}"
        }
    }
    data class ExamPreview(val examId: Long) : Screen("exam_preview/$examId") {
        companion object {
            const val ROUTE = "exam_preview/{examId}"
        }
    }
    data object QuestionBank : Screen("question_bank")
    data object Settings : Screen("settings")
}
