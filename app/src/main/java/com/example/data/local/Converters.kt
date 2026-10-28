package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ExamHeader
import com.example.data.model.ExamSection
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val sectionListType = Types.newParameterizedType(List::class.java, ExamSection::class.java)
    private val sectionListAdapter = moshi.adapter<List<ExamSection>>(sectionListType)
    private val headerAdapter = moshi.adapter(ExamHeader::class.java)
    private val subQuestionAdapter = moshi.adapter(SubQuestion::class.java)

    @TypeConverter
    fun fromExamHeader(header: ExamHeader?): String {
        return header?.let { headerAdapter.toJson(it) } ?: ""
    }

    @TypeConverter
    fun toExamHeader(json: String?): ExamHeader {
        return if (json.isNullOrEmpty()) ExamHeader() else headerAdapter.fromJson(json) ?: ExamHeader()
    }

    @TypeConverter
    fun fromSectionsList(sections: List<ExamSection>?): String {
        return sections?.let { sectionListAdapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toSectionsList(json: String?): List<ExamSection> {
        return if (json.isNullOrEmpty()) emptyList() else sectionListAdapter.fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromSubQuestion(sub: SubQuestion?): String {
        return sub?.let { subQuestionAdapter.toJson(it) } ?: ""
    }

    @TypeConverter
    fun toSubQuestion(json: String?): SubQuestion {
        return if (json.isNullOrEmpty()) SubQuestion() else subQuestionAdapter.fromJson(json) ?: SubQuestion()
    }

    @TypeConverter
    fun fromQuestionType(type: QuestionType?): String {
        return type?.name ?: QuestionType.DEFINITIONS.name
    }

    @TypeConverter
    fun toQuestionType(value: String?): QuestionType {
        return try {
            QuestionType.valueOf(value ?: QuestionType.DEFINITIONS.name)
        } catch (_: Exception) {
            QuestionType.DEFINITIONS
        }
    }
}
