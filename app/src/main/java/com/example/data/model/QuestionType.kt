package com.example.data.model

enum class SubjectType(val arabicName: String, val englishName: String, val defaultLtr: Boolean = false) {
    ISLAMIC_EDUCATION("التربية الإسلامية", "Islamic Education"),
    ARABIC_LANGUAGE("اللغة العربية", "Arabic Language"),
    ENGLISH_LANGUAGE("اللغة الإنجليزية", "English Language", true),
    MATHEMATICS("الرياضيات", "Mathematics"),
    SCIENCES("العلوم العامة", "General Science"),
    PHYSICS("الفيزياء", "Physics"),
    CHEMISTRY("الكيمياء", "Chemistry"),
    BIOLOGY("الأحياء", "Biology"),
    HISTORY("التاريخ", "History"),
    GEOGRAPHY("الجغرافيا", "Geography"),
    NATIONAL_EDUCATION("التربية الوطنية والاجتماعيات", "Social Studies"),
    COMPUTER("الحاسوب", "Computer Science"),
    OTHER("مادة أخرى", "Other");

    companion object {
        fun fromArabicName(name: String): SubjectType {
            return entries.find { it.arabicName == name } ?: OTHER
        }
    }
}

enum class GradeStage(val arabicName: String) {
    PRIMARY("المرحلة الابتدائية"),
    INTERMEDIATE("المرحلة المتوسطة"),
    PREPARATORY("المرحلة الإعدادية")
}

enum class QuestionType(val titleAr: String, val categoryAr: String) {
    DEFINITIONS("تعريفات (ما المقصود)", "حفظ ومفاهيم"),
    MULTIPLE_CHOICE("اختيار من متعدد", "موضوعي"),
    FILL_IN_BLANKS("أكمل الفراغات (...........)", "موضوعي"),
    TRUE_FALSE("صح أو خطأ (مع التصحيح)", "موضوعي"),
    SHORT_ESSAY("أسئلة مقالية قصيرة (أجب عما يأتي)", "مقالي"),
    REASONING_COMPARISON("علل / ما الفرق / قارن / ما فائدة", "تحليلي ومقالي"),
    MATCHING("وصّل بين العمودين (أ) و(ب)", "مطابقة وقوائم"),
    REORDER("رتّب الكلمات أو الخطوات", "ترتيب ومنطق"),
    MATH_PROBLEM("مسائل حسابية ومعادلات", "رياضي وعلمي"),
    DRAWING("ارسم / ارسم مع التأشير", "رسم ومخططات"),
    IMAGE_BASED("سؤال مدعوم بصورة أو خريطة", "بصري وتطبيقي"),
    READING_COMPREHENSION("قطعة قراءة / نص أدبي", "نصوص وقراءة"),
    ENGLISH_LTR("سؤال لغة إنجليزية (LTR)", "لغات أجنبية")
}
