package com.example.data.local

import androidx.room.*
import com.example.data.model.Exam
import com.example.data.model.QuestionBankItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY updatedAt DESC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE isTemplate = 1 ORDER BY updatedAt DESC")
    fun getTemplates(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :examId")
    suspend fun getExamById(examId: Long): Exam?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExamById(examId: Long)
}

@Dao
interface QuestionBankDao {
    @Query("SELECT * FROM question_bank ORDER BY createdAt DESC")
    fun getAllQuestions(): Flow<List<QuestionBankItem>>

    @Query("SELECT * FROM question_bank WHERE subject = :subject ORDER BY createdAt DESC")
    fun getQuestionsBySubject(subject: String): Flow<List<QuestionBankItem>>

    @Query("SELECT * FROM question_bank WHERE subject = :subject AND grade = :grade ORDER BY createdAt DESC")
    fun getQuestionsBySubjectAndGrade(subject: String, grade: String): Flow<List<QuestionBankItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(item: QuestionBankItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(items: List<QuestionBankItem>)

    @Update
    suspend fun updateQuestion(item: QuestionBankItem)

    @Delete
    suspend fun deleteQuestion(item: QuestionBankItem)

    @Query("DELETE FROM question_bank WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)
}
