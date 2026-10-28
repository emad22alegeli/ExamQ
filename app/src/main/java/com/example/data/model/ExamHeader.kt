package com.example.data.model

data class ExamHeader(
    val ministry: String = "جمهورية العراق - وزارة التربية",
    val directorate: String = "المديرية العامة للتربية في بغداد",
    val schoolName: String = "ثانوية المتميزين للبنين",
    val examTitle: String = "أسئلة امتحان نصف السنة للعام الدراسي",
    val academicYear: String = "2024 - 2025 م",
    val subject: String = "العلوم العامة",
    val grade: String = "الثالث المتوسط",
    val examDate: String = "2025/01/15",
    val duration: String = "ساعتان",
    val generalInstruction: String = "ملاحظة: أجب عن خمسة أسئلة فقط ولكل سؤال (20) درجة",
    val showStudentInfoRow: Boolean = true,
    val teacherName: String = "أستاذ المادة",
    val closingWish: String = "مع تمنياتي لكم بالنجاح والتفوق",
    val examGroup: String = "A" // "A" for نموذج (أ) or "B" for نموذج (ب)
)
