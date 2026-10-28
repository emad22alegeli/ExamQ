package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Exam
import com.example.data.model.ExamHeader
import com.example.data.model.ExamSection
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("منشئ الامتحانات", appName)
    }

    @Test
    fun `calculate total score matches sum of section subquestions`() {
        val exam = Exam(
            title = "امتحان تجريبي",
            subject = "الفيزياء",
            grade = "الثالث المتوسط",
            totalTargetScore = 100.0,
            sections = listOf(
                ExamSection(
                    title = "س1:",
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", score = 10.0),
                        SubQuestion(label = "2-", score = 10.0)
                    )
                ),
                ExamSection(
                    title = "س2:",
                    sectionScore = 30.0,
                    subQuestions = listOf(
                        SubQuestion(label = "أ-", score = 15.0),
                        SubQuestion(label = "ب-", score = 15.0)
                    )
                )
            )
        )

        assertEquals(50.0, exam.calculateTotalCalculatedScore(), 0.01)
    }

    @Test
    fun `createGroupB produces Group B tag and reverses question order`() {
        val exam = Exam(
            title = "امتحان الكيمياء",
            header = ExamHeader(examGroup = "A"),
            sections = listOf(
                ExamSection(
                    title = "س1:",
                    type = QuestionType.MULTIPLE_CHOICE,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "سؤال أول", options = listOf("أ", "ب", "ج")),
                        SubQuestion(label = "2-", text = "سؤال ثاني", options = listOf("1", "2", "3"))
                    )
                )
            )
        )

        val groupB = exam.createGroupB()
        assertEquals("B", groupB.header.examGroup)
        assertEquals(1, groupB.sections.size)
        assertEquals(2, groupB.sections.first().subQuestions.size)
        assertEquals("سؤال ثاني", groupB.sections.first().subQuestions.first().text)
    }

    @Test
    fun testMainActivityLaunch() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }
}
