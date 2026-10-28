package com.example.data.repository

import android.content.Context
import com.example.data.local.ExamDao
import com.example.data.local.QuestionBankDao
import com.example.data.model.Exam
import com.example.data.model.ExamHeader
import com.example.data.model.QuestionBankItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class BackupContainer(
    val exportDate: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0",
    val exams: List<Exam> = emptyList(),
    val questionBank: List<QuestionBankItem> = emptyList()
)

class ExamRepository(
    private val examDao: ExamDao,
    private val questionBankDao: QuestionBankDao,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("iraqi_exam_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val allExams: Flow<List<Exam>> = examDao.getAllExams()
    val allQuestions: Flow<List<QuestionBankItem>> = questionBankDao.getAllQuestions()

    suspend fun getExamById(id: Long): Exam? = withContext(Dispatchers.IO) {
        examDao.getExamById(id)
    }

    suspend fun saveExam(exam: Exam): Long = withContext(Dispatchers.IO) {
        val updated = exam.copy(updatedAt = System.currentTimeMillis())
        if (exam.id == 0L) {
            examDao.insertExam(updated)
        } else {
            examDao.updateExam(updated)
            exam.id
        }
    }

    suspend fun duplicateExam(exam: Exam, asTemplate: Boolean = false): Long = withContext(Dispatchers.IO) {
        val newTitle = if (asTemplate) "قالب: ${exam.title}" else "نسخة من ${exam.title}"
        val copy = exam.copy(
            id = 0,
            title = newTitle,
            isTemplate = asTemplate,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        examDao.insertExam(copy)
    }

    suspend fun deleteExam(exam: Exam) = withContext(Dispatchers.IO) {
        examDao.deleteExam(exam)
    }

    suspend fun deleteExamById(id: Long) = withContext(Dispatchers.IO) {
        examDao.deleteExamById(id)
    }

    // Question Bank operations
    suspend fun addQuestionToBank(item: QuestionBankItem): Long = withContext(Dispatchers.IO) {
        questionBankDao.insertQuestion(item)
    }

    suspend fun deleteQuestionFromBank(id: Long) = withContext(Dispatchers.IO) {
        questionBankDao.deleteQuestionById(id)
    }

    // Default Teacher / School Profile
    fun getDefaultHeader(): ExamHeader {
        return ExamHeader(
            ministry = prefs.getString("ministry", "جمهورية العراق - وزارة التربية") ?: "جمهورية العراق - وزارة التربية",
            directorate = prefs.getString("directorate", "المديرية العامة للتربية في بغداد") ?: "المديرية العامة للتربية في بغداد",
            schoolName = prefs.getString("schoolName", "ثانوية المتميزين للبنين") ?: "ثانوية المتميزين للبنين",
            teacherName = prefs.getString("teacherName", "أستاذ المادة") ?: "أستاذ المادة",
            academicYear = prefs.getString("academicYear", "2024 - 2025 م") ?: "2024 - 2025 م",
            closingWish = prefs.getString("closingWish", "مع تمنياتي لكم بالنجاح والتفوق") ?: "مع تمنياتي لكم بالنجاح والتفوق"
        )
    }

    fun saveDefaultProfile(
        ministry: String,
        directorate: String,
        schoolName: String,
        teacherName: String,
        academicYear: String,
        closingWish: String
    ) {
        prefs.edit()
            .putString("ministry", ministry)
            .putString("directorate", directorate)
            .putString("schoolName", schoolName)
            .putString("teacherName", teacherName)
            .putString("academicYear", academicYear)
            .putString("closingWish", closingWish)
            .apply()
    }

    // Backup & Restore
    suspend fun exportToJson(exams: List<Exam>, questions: List<QuestionBankItem>): String = withContext(Dispatchers.IO) {
        val container = BackupContainer(exams = exams, questionBank = questions)
        val adapter = moshi.adapter(BackupContainer::class.java)
        adapter.toJson(container)
    }

    suspend fun importFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val adapter = moshi.adapter(BackupContainer::class.java)
            val container = adapter.fromJson(jsonString) ?: return@withContext Result.failure(Exception("ملف غير صالح"))

            var count = 0
            container.exams.forEach { exam ->
                examDao.insertExam(exam.copy(id = 0))
                count++
            }
            container.questionBank.forEach { q ->
                questionBankDao.insertQuestion(q.copy(id = 0))
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
