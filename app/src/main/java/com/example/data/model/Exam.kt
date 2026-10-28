package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "امتحان جديد",
    val subject: String = "العلوم",
    val grade: String = "الثالث المتوسط",
    val totalTargetScore: Double = 100.0,
    val header: ExamHeader = ExamHeader(),
    val sections: List<ExamSection> = emptyList(),
    val isTemplate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculates the sum of all section scores in the exam
     */
    fun calculateTotalCalculatedScore(): Double {
        return sections.sumOf { section ->
            if (section.subQuestions.isNotEmpty()) {
                val subSum = section.subQuestions.sumOf { it.score }
                if (subSum > 0) subSum else section.sectionScore
            } else {
                section.sectionScore
            }
        }
    }

    /**
     * Creates Group B (نموذج ب) with shuffled subquestions and multiple choice options
     */
    fun createGroupB(): Exam {
        val newSections = sections.map { section ->
            val shuffledSubs = section.subQuestions.reversed().mapIndexed { index, sub ->
                val newLabel = when {
                    sub.label.contains("-") -> "${index + 1}-"
                    sub.label.contains("أ") || sub.label.contains("ب") || sub.label.contains("ج") -> {
                        val arabicLetters = listOf("أ-", "ب-", "ج-", "د-", "هـ-", "و-")
                        arabicLetters.getOrElse(index) { "${index + 1}-" }
                    }
                    else -> "${index + 1}-"
                }
                val newOptions = if (sub.options.size > 1) sub.options.shuffled() else sub.options
                sub.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    label = newLabel,
                    options = newOptions
                )
            }
            section.copy(
                id = java.util.UUID.randomUUID().toString(),
                subQuestions = shuffledSubs
            )
        }

        return this.copy(
            header = this.header.copy(examGroup = "B"),
            sections = newSections
        )
    }
}

@Entity(tableName = "question_bank")
data class QuestionBankItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String = "العلوم",
    val grade: String = "الثالث المتوسط",
    val chapter: String = "الفصل الأول",
    val difficulty: String = "متوسط", // سهل, متوسط, صعب
    val type: QuestionType = QuestionType.DEFINITIONS,
    val subQuestion: SubQuestion = SubQuestion(),
    val createdAt: Long = System.currentTimeMillis()
)
