package com.example.data.model

data class MatchingPair(
    val id: String = java.util.UUID.randomUUID().toString(),
    val columnA: String = "",
    val columnB: String = "",
    val correctMatch: String = "" // Answer key, e.g. "1 -> ج"
)

data class SubQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "1-",
    val text: String = "",
    val options: List<String> = emptyList(), // For multiple choice
    val matchingPairs: List<MatchingPair> = emptyList(), // For matching
    val drawingHeightDp: Int = 120, // For drawing space in A4 sheet
    val imageUri: String? = null, // Visual asset
    val passageText: String? = null, // Comprehension passage
    val isLtr: Boolean = false,
    val score: Double = 5.0,
    val modelAnswer: String = "",
    val note: String = ""
)

data class ExamSection(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "س1:",
    val instruction: String = "أجب عما يأتي:",
    val type: QuestionType = QuestionType.DEFINITIONS,
    val sectionScore: Double = 20.0,
    val subQuestions: List<SubQuestion> = emptyList(),
    val isLtr: Boolean = false,
    val note: String = "" // e.g. "أجب عن أربعة أفرع فقط لكل فرع 5 درجات"
)
