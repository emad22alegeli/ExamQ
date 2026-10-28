package com.example.domain

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.Exam
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import java.io.File
import java.io.FileOutputStream

class ExamPdfGenerator(private val context: Context) {

    private val pageWidth = 595 // Standard A4 width in points
    private val pageHeight = 842 // Standard A4 height in points
    private val margin = 28f

    fun generatePdf(exam: Exam, isModelAnswer: Boolean = false): File {
        val pdfDocument = PdfDocument()
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }

        val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 10.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val headerTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        val lightBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 0.6f
        }

        // Draw initial page frame
        drawPageBorder(canvas, borderPaint)
        var currentY = drawHeader(canvas, exam, headerTitlePaint, boldPaint, textPaint, lightBorderPaint, isModelAnswer)

        // Draw Questions or Model Answers
        for (section in exam.sections) {
            // Check if we need a new page
            if (currentY > pageHeight - 120) {
                drawFooter(canvas, exam, boldPaint, textPaint, lightBorderPaint, pageNumber)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                drawPageBorder(canvas, borderPaint)
                currentY = margin + 30f
            }

            // Draw Section Title e.g. "السؤال الأول (20 درجة): أجب عما يأتي:"
            val sectionHeader = "${section.title} (${section.sectionScore.toInt()} درجة)  ${section.instruction}"
            canvas.drawText(sectionHeader, pageWidth - margin - 15f, currentY, boldPaint.apply { textAlign = Paint.Align.RIGHT })
            currentY += 16f

            if (section.note.isNotBlank()) {
                canvas.drawText("[${section.note}]", pageWidth - margin - 25f, currentY, textPaint.apply { textAlign = Paint.Align.RIGHT })
                currentY += 14f
            }

            // Draw SubQuestions
            for (sub in section.subQuestions) {
                if (currentY > pageHeight - 110) {
                    drawFooter(canvas, exam, boldPaint, textPaint, lightBorderPaint, pageNumber)
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    drawPageBorder(canvas, borderPaint)
                    currentY = margin + 30f
                }

                // SubQuestion text
                val scoreTag = if (sub.score > 0) " (${sub.score.toInt()} درجات)" else ""
                val fullQuestionLine = "${sub.label} ${sub.text}$scoreTag"

                // Check text direction
                if (section.isLtr || sub.isLtr) {
                    canvas.drawText(fullQuestionLine, margin + 15f, currentY, textPaint.apply { textAlign = Paint.Align.LEFT })
                } else {
                    canvas.drawText(fullQuestionLine, pageWidth - margin - 25f, currentY, textPaint.apply { textAlign = Paint.Align.RIGHT })
                }
                currentY += 15f

                // If Multiple Choice options exist
                if (sub.options.isNotEmpty()) {
                    val optionsFormatted = "( " + sub.options.mapIndexed { idx, opt ->
                        val letter = listOf("أ", "ب", "ج", "د", "هـ").getOrElse(idx) { "$idx" }
                        "$letter- $opt"
                    }.joinToString("  -  ") + " )"
                    canvas.drawText(optionsFormatted, pageWidth - margin - 35f, currentY, textPaint.apply { textAlign = Paint.Align.RIGHT })
                    currentY += 14f
                }

                // If Matching Pairs exist
                if (sub.matchingPairs.isNotEmpty()) {
                    currentY = drawMatchingTable(canvas, sub.matchingPairs, currentY, textPaint, lightBorderPaint)
                }

                // If Drawing question
                if (section.type == QuestionType.DRAWING || sub.drawingHeightDp > 0 && section.type == QuestionType.DRAWING) {
                    val boxHeight = (sub.drawingHeightDp * 0.4f).coerceIn(40f, 90f)
                    val boxRect = Rect((margin + 20).toInt(), currentY.toInt(), (pageWidth - margin - 20).toInt(), (currentY + boxHeight).toInt())
                    canvas.drawRect(boxRect, lightBorderPaint)
                    canvas.drawText("[مساحة مخصصة للرسم والتأشير]", (pageWidth / 2).toFloat(), currentY + (boxHeight / 2) + 4, textPaint.apply { textAlign = Paint.Align.CENTER })
                    currentY += boxHeight + 12f
                }

                // If Model Answer is requested
                if (isModelAnswer && sub.modelAnswer.isNotBlank()) {
                    val ansText = "← الإجابة النموذجية: ${sub.modelAnswer}"
                    canvas.drawText(ansText, pageWidth - margin - 35f, currentY, boldPaint.apply { textAlign = Paint.Align.RIGHT })
                    currentY += 14f
                }
            }

            // Divider line between sections
            canvas.drawLine(margin + 30f, currentY + 3f, pageWidth - margin - 30f, currentY + 3f, lightBorderPaint)
            currentY += 14f
        }

        // Draw Final Footer on last page
        drawFooter(canvas, exam, boldPaint, textPaint, lightBorderPaint, pageNumber)
        pdfDocument.finishPage(page)

        // Save PDF to cache
        val prefix = if (isModelAnswer) "model_answer_" else "exam_"
        val cleanName = exam.title.replace("\\s+".toRegex(), "_")
        val outputFile = File(context.cacheDir, "${prefix}${cleanName}_${System.currentTimeMillis()}.pdf")
        val outputStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()

        return outputFile
    }

    private fun drawPageBorder(canvas: Canvas, paint: Paint) {
        // Outer decorative double border
        val outerRect = Rect(margin.toInt(), margin.toInt(), (pageWidth - margin).toInt(), (pageHeight - margin).toInt())
        canvas.drawRect(outerRect, paint)
        val innerRect = Rect((margin + 3).toInt(), (margin + 3).toInt(), (pageWidth - margin - 3).toInt(), (pageHeight - margin - 3).toInt())
        canvas.drawRect(innerRect, paint)
    }

    private fun drawHeader(
        canvas: Canvas,
        exam: Exam,
        titlePaint: Paint,
        boldPaint: Paint,
        textPaint: Paint,
        borderPaint: Paint,
        isModelAnswer: Boolean
    ): Float {
        var y = margin + 18f
        val h = exam.header

        // Top Right Info (الجمهورية والوزارة والمدرسة)
        boldPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(h.ministry, pageWidth - margin - 15f, y, boldPaint)
        canvas.drawText(h.directorate, pageWidth - margin - 15f, y + 13f, textPaint)
        canvas.drawText("مدرسة: ${h.schoolName}", pageWidth - margin - 15f, y + 26f, textPaint)

        // Center Info ("بسمه تعالى" وعنوان الامتحان والنموذج)
        canvas.drawText("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", (pageWidth / 2).toFloat(), y - 4f, boldPaint.apply { textAlign = Paint.Align.CENTER })
        val titleLine = if (isModelAnswer) "الإجابة النموذجية وسلّم الدرجات" else h.examTitle
        canvas.drawText(titleLine, (pageWidth / 2).toFloat(), y + 11f, titlePaint)
        canvas.drawText("للعام الدراسي: ${h.academicYear}", (pageWidth / 2).toFloat(), y + 25f, textPaint.apply { textAlign = Paint.Align.CENTER })

        // Exam Group Badge (نموذج أ أو ب)
        val groupText = if (h.examGroup == "B") "[ نموذج (ب) ]" else "[ نموذج (أ) ]"
        canvas.drawText(groupText, (pageWidth / 2).toFloat(), y + 37f, boldPaint.apply { textAlign = Paint.Align.CENTER })

        // Top Left Info (المادة، الصف، التاريخ، الزمن)
        boldPaint.textAlign = Paint.Align.LEFT
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("المادة: ${h.subject}", margin + 15f, y, boldPaint)
        canvas.drawText("الصف: ${h.grade}", margin + 15f, y + 13f, textPaint)
        canvas.drawText("التاريخ: ${h.examDate}", margin + 15f, y + 26f, textPaint)
        canvas.drawText("الزمن: ${h.duration}", margin + 15f, y + 38f, textPaint)

        y += 46f
        // Horizontal divider
        canvas.drawLine(margin + 5f, y, pageWidth - margin - 5f, y, borderPaint)
        y += 12f

        // Student Info Row if enabled
        if (h.showStudentInfoRow && !isModelAnswer) {
            textPaint.textAlign = Paint.Align.RIGHT
            val studentRow = "اسم الطالب: ..............................................................   الرقم الامتحاني: ........................   القاعة: ............."
            canvas.drawText(studentRow, pageWidth - margin - 15f, y, textPaint)
            y += 12f
            canvas.drawLine(margin + 5f, y, pageWidth - margin - 5f, y, borderPaint)
            y += 12f
        }

        // General note
        if (h.generalInstruction.isNotBlank()) {
            boldPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(h.generalInstruction, (pageWidth / 2).toFloat(), y, boldPaint)
            y += 10f
            canvas.drawLine(margin + 15f, y, pageWidth - margin - 15f, y, borderPaint)
            y += 14f
        }

        return y
    }

    private fun drawMatchingTable(
        canvas: Canvas,
        pairs: List<com.example.data.model.MatchingPair>,
        startY: Float,
        textPaint: Paint,
        borderPaint: Paint
    ): Float {
        var y = startY
        textPaint.textAlign = Paint.Align.RIGHT

        // Header for columns
        val midX = (pageWidth / 2).toFloat()
        canvas.drawText("العمود (أ)", pageWidth - margin - 40f, y, textPaint)
        canvas.drawText("العمود (ب)", midX - 20f, y, textPaint)
        y += 13f

        for (pair in pairs) {
            canvas.drawText(pair.columnA, pageWidth - margin - 40f, y, textPaint)
            canvas.drawText(pair.columnB, midX - 20f, y, textPaint)
            y += 13f
        }
        return y
    }

    private fun drawFooter(
        canvas: Canvas,
        exam: Exam,
        boldPaint: Paint,
        textPaint: Paint,
        borderPaint: Paint,
        pageNumber: Int
    ) {
        val y = pageHeight - margin - 15f
        // Line above footer
        canvas.drawLine(margin + 5f, y - 18f, pageWidth - margin - 5f, y - 18f, borderPaint)

        // Teacher signature (Right in RTL)
        boldPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("مدرس المادة: ${exam.header.teacherName}", pageWidth - margin - 15f, y - 4f, boldPaint)

        // Central Wish
        boldPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(exam.header.closingWish, (pageWidth / 2).toFloat(), y - 4f, boldPaint)

        // School Admin (Left)
        boldPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("إدارة المدرسة", margin + 15f, y - 4f, boldPaint)

        // Page number
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 8.5f
        canvas.drawText("- صفحة $pageNumber -", (pageWidth / 2).toFloat(), y + 8f, textPaint)
        textPaint.textSize = 10f
    }

    fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "مشاركة ورقة الامتحان بصيغة PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
