package com.example.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExamDatabase
import com.example.data.model.*
import com.example.data.repository.ExamRepository
import com.example.domain.ExamAiAssistant
import com.example.domain.ExamPdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ExamDatabase.getDatabase(application, viewModelScope)
    private val repository = ExamRepository(database.examDao(), database.questionBankDao(), application)
    private val pdfGenerator = ExamPdfGenerator(application)
    private val aiAssistant = ExamAiAssistant()

    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams.asStateFlow()

    private val _questionBank = MutableStateFlow<List<QuestionBankItem>>(emptyList())
    val questionBank: StateFlow<List<QuestionBankItem>> = _questionBank.asStateFlow()

    private val _currentExam = MutableStateFlow<Exam?>(null)
    val currentExam: StateFlow<Exam?> = _currentExam.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val _defaultHeader = MutableStateFlow(repository.getDefaultHeader())
    val defaultHeader: StateFlow<ExamHeader> = _defaultHeader.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allExams.collect { list ->
                _exams.value = list
            }
        }
        viewModelScope.launch {
            repository.allQuestions.collect { list ->
                _questionBank.value = list
            }
        }
    }

    fun loadExam(examId: Long) {
        viewModelScope.launch {
            val exam = repository.getExamById(examId)
            _currentExam.value = exam
        }
    }

    fun createNewExam(
        title: String,
        subject: String,
        grade: String,
        targetScore: Double = 100.0,
        examTitleType: String = "أسئلة امتحان نصف السنة"
    ): Long {
        val defaultH = _defaultHeader.value
        val initialHeader = defaultH.copy(
            subject = subject,
            grade = grade,
            examTitle = examTitleType
        )

        // Seed 2 default sections based on subject
        val initialSections = listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "ما المقصود باثنين فقط مما يأتي:",
                type = QuestionType.DEFINITIONS,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "المفهوم الأول في مادة $subject", score = 10.0),
                    SubQuestion(label = "2-", text = "المفهوم الثاني", score = 10.0)
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "اختر الإجابة الصحيحة من بين الأقواس:",
                type = QuestionType.MULTIPLE_CHOICE,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "العبارة الأولى هنا:", options = listOf("أ", "ب", "ج", "د"), score = 10.0),
                    SubQuestion(label = "2-", text = "العبارة الثانية هنا:", options = listOf("أ", "ب", "ج", "د"), score = 10.0)
                )
            )
        )

        val newExam = Exam(
            id = 0,
            title = title,
            subject = subject,
            grade = grade,
            totalTargetScore = targetScore,
            header = initialHeader,
            sections = initialSections
        )

        var createdId = 0L
        viewModelScope.launch {
            createdId = repository.saveExam(newExam)
            loadExam(createdId)
        }
        return createdId
    }

    fun updateCurrentExam(exam: Exam) {
        _currentExam.value = exam
        viewModelScope.launch {
            repository.saveExam(exam)
        }
    }

    fun updateHeader(header: ExamHeader) {
        _currentExam.value?.let { current ->
            val updated = current.copy(header = header)
            updateCurrentExam(updated)
        }
    }

    fun updateTargetScore(score: Double) {
        _currentExam.value?.let { current ->
            val updated = current.copy(totalTargetScore = score)
            updateCurrentExam(updated)
        }
    }

    fun addSection(type: QuestionType) {
        _currentExam.value?.let { current ->
            val nextNum = current.sections.size + 1
            val arabicNums = listOf("الأول", "الثاني", "الثالث", "الرابع", "الخامس", "السادس", "السابع", "الثامن")
            val numWord = arabicNums.getOrElse(nextNum - 1) { "$nextNum" }

            val newSection = when (type) {
                QuestionType.DEFINITIONS -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "ما المقصود باثنين فقط مما يأتي:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "المصطلح الأول:", score = 10.0),
                        SubQuestion(label = "2-", text = "المصطلح الثاني:", score = 10.0)
                    )
                )
                QuestionType.MULTIPLE_CHOICE -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "اختر الإجابة الصحيحة بين الأقواس لما يأتي:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "السؤال الأول:", options = listOf("خيار 1", "خيار 2", "خيار 3", "خيار 4"), score = 5.0),
                        SubQuestion(label = "2-", text = "السؤال الثاني:", options = listOf("خيار 1", "خيار 2", "خيار 3", "خيار 4"), score = 5.0)
                    )
                )
                QuestionType.FILL_IN_BLANKS -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "أكمل الفراغات الآتية بما يناسبها علمياً ولأربع فقط:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "يحدث التفاعل عندما ............. مع .............", score = 5.0),
                        SubQuestion(label = "2-", text = "تقاس وحدة القوة بـ ............. ووحدة الزمن بـ .............", score = 5.0)
                    )
                )
                QuestionType.TRUE_FALSE -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "ضع علامة (✓) أو (✗) مع تصحيح الخطأ إن وجد:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "العبارة الأولى العلمية أو الأدبية هنا.", score = 10.0),
                        SubQuestion(label = "2-", text = "العبارة الثانية العلمية أو الأدبية هنا.", score = 10.0)
                    )
                )
                QuestionType.MATCHING -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "وصّل بين مفردات العمود (أ) بما يناسبها من العمود (ب):",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(
                            label = "1-",
                            text = "طابق بين القائمتين الآتيتين:",
                            matchingPairs = listOf(
                                MatchingPair(columnA = "1. العنصر أ", columnB = "أ. المطابق له"),
                                MatchingPair(columnA = "2. العنصر ب", columnB = "ب. المطابق له")
                            ),
                            score = 20.0
                        )
                    )
                )
                QuestionType.MATH_PROBLEM -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "مسألة حسابية: أجب عما يأتي مع كتابة القوانين والوحدات بالتفصيل:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "أ-", text = "أوجد ناتج العملية الحسابية الآتية بالتفصيل: f(x) = ...", score = 10.0),
                        SubQuestion(label = "ب-", text = "احسب المقدار الآتي: ...", score = 10.0)
                    )
                )
                QuestionType.DRAWING -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "ارسم مع التأشير الكامل على الأجزاء:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "ارسم رسماً توضيحياً يبيّن تركيب الجهاز مع التأشير على أجزائه الرئيسية.", drawingHeightDp = 150, score = 20.0)
                    )
                )
                QuestionType.ENGLISH_LTR -> ExamSection(
                    title = "Question $nextNum (20 Marks)",
                    instruction = "Do as required between brackets for each sentence:",
                    type = type,
                    sectionScore = 20.0,
                    isLtr = true,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "Complete the sentence with correct form: ...", isLtr = true, score = 10.0),
                        SubQuestion(label = "2-", text = "Choose the correct preposition: ...", isLtr = true, score = 10.0)
                    )
                )
                else -> ExamSection(
                    title = "السؤال $numWord (20 درجة):",
                    instruction = "أجب عما يأتي:",
                    type = type,
                    sectionScore = 20.0,
                    subQuestions = listOf(
                        SubQuestion(label = "1-", text = "الفقرة الأولى هنا", score = 10.0),
                        SubQuestion(label = "2-", text = "الفقرة الثانية هنا", score = 10.0)
                    )
                )
            }

            val updated = current.copy(sections = current.sections + newSection)
            updateCurrentExam(updated)
        }
    }

    fun reorderSection(fromIndex: Int, toIndex: Int) {
        _currentExam.value?.let { current ->
            if (fromIndex in current.sections.indices && toIndex in current.sections.indices) {
                val list = current.sections.toMutableList()
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
                updateCurrentExam(current.copy(sections = list))
            }
        }
    }

    fun duplicateSection(sectionIndex: Int) {
        _currentExam.value?.let { current ->
            if (sectionIndex in current.sections.indices) {
                val sectionToDup = current.sections[sectionIndex]
                val copiedSection = sectionToDup.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    title = "${sectionToDup.title} (نسخة)",
                    subQuestions = sectionToDup.subQuestions.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                )
                val list = current.sections.toMutableList()
                list.add(sectionIndex + 1, copiedSection)
                updateCurrentExam(current.copy(sections = list))
            }
        }
    }

    fun deleteSection(sectionIndex: Int) {
        _currentExam.value?.let { current ->
            if (sectionIndex in current.sections.indices) {
                val list = current.sections.toMutableList()
                list.removeAt(sectionIndex)
                updateCurrentExam(current.copy(sections = list))
            }
        }
    }

    fun duplicateExam(exam: Exam, asTemplate: Boolean = false) {
        viewModelScope.launch {
            repository.duplicateExam(exam, asTemplate)
            Toast.makeText(getApplication(), "تم إنشاء نسخة من الامتحان بنجاح", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.deleteExam(exam)
            if (_currentExam.value?.id == exam.id) {
                _currentExam.value = null
            }
            Toast.makeText(getApplication(), "تم حذف الامتحان", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveQuestionToBank(subQuestion: SubQuestion, type: QuestionType) {
        val current = _currentExam.value ?: return
        val bankItem = QuestionBankItem(
            subject = current.subject,
            grade = current.grade,
            chapter = "أسئلة مضافة",
            difficulty = "متوسط",
            type = type,
            subQuestion = subQuestion
        )
        viewModelScope.launch {
            repository.addQuestionToBank(bankItem)
            Toast.makeText(getApplication(), "تمت إضافة السؤال إلى بنك الأسئلة", Toast.LENGTH_SHORT).show()
        }
    }

    fun addQuestionBankItem(item: QuestionBankItem) {
        viewModelScope.launch {
            repository.addQuestionToBank(item)
            Toast.makeText(getApplication(), "تمت إضافة السؤال إلى بنك الأسئلة", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteQuestionFromBank(id: Long) {
        viewModelScope.launch {
            repository.deleteQuestionFromBank(id)
            Toast.makeText(getApplication(), "تم حذف السؤال من البنك", Toast.LENGTH_SHORT).show()
        }
    }

    fun importQuestionFromBankToExam(bankItem: QuestionBankItem) {
        val current = _currentExam.value ?: return
        val sectionToAddTo = current.sections.find { it.type == bankItem.type }
        if (sectionToAddTo != null) {
            val nextIdx = sectionToAddTo.subQuestions.size + 1
            val newSub = bankItem.subQuestion.copy(
                id = java.util.UUID.randomUUID().toString(),
                label = "${nextIdx}-"
            )
            val updatedSections = current.sections.map { sec ->
                if (sec.id == sectionToAddTo.id) {
                    sec.copy(subQuestions = sec.subQuestions + newSub)
                } else sec
            }
            updateCurrentExam(current.copy(sections = updatedSections))
        } else {
            // Create a new section for it
            val arabicNums = listOf("الأول", "الثاني", "الثالث", "الرابع", "الخامس", "السادس")
            val nextNumWord = arabicNums.getOrElse(current.sections.size) { "${current.sections.size + 1}" }
            val newSection = ExamSection(
                title = "السؤال $nextNumWord:",
                instruction = bankItem.type.titleAr,
                type = bankItem.type,
                sectionScore = bankItem.subQuestion.score.coerceAtLeast(10.0),
                subQuestions = listOf(bankItem.subQuestion.copy(id = java.util.UUID.randomUUID().toString(), label = "1-"))
            )
            updateCurrentExam(current.copy(sections = current.sections + newSection))
        }
        Toast.makeText(getApplication(), "تم إدراج السؤال في الامتحان بنجاح", Toast.LENGTH_SHORT).show()
    }

    fun generateAiQuestions(topic: String) {
        val current = _currentExam.value ?: return
        viewModelScope.launch {
            _isGeneratingAi.value = true
            try {
                val generated = aiAssistant.generateQuestionsForSubject(
                    subject = current.subject,
                    grade = current.grade,
                    lessonTopic = topic
                )
                if (generated.isNotEmpty()) {
                    updateCurrentExam(current.copy(sections = current.sections + generated))
                    Toast.makeText(getApplication(), "تم توليد ${generated.size} أسئلة بنجاح بالذكاء الاصطناعي", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "حدث خطأ أثناء التوليد: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isGeneratingAi.value = false
            }
        }
    }

    fun generateAndSharePdf(exam: Exam, isModelAnswer: Boolean = false) {
        viewModelScope.launch {
            try {
                val file = pdfGenerator.generatePdf(exam, isModelAnswer)
                pdfGenerator.sharePdf(file)
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "تعذر إنشاء ملف PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveProfileSettings(
        ministry: String,
        directorate: String,
        schoolName: String,
        teacherName: String,
        academicYear: String,
        closingWish: String
    ) {
        repository.saveDefaultProfile(
            ministry = ministry,
            directorate = directorate,
            schoolName = schoolName,
            teacherName = teacherName,
            academicYear = academicYear,
            closingWish = closingWish
        )
        _defaultHeader.value = repository.getDefaultHeader()
        Toast.makeText(getApplication(), "تم حفظ الإعدادات الافتراضية بنجاح", Toast.LENGTH_SHORT).show()
    }

    fun exportBackup(onExportReady: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportToJson(_exams.value, _questionBank.value)
            onExportReady(json)
        }
    }

    fun importBackup(json: String) {
        viewModelScope.launch {
            val result = repository.importFromJson(json)
            result.onSuccess { count ->
                Toast.makeText(getApplication(), "تم استرجاع $count امتحانات بنجاح", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(getApplication(), "فشل الاسترجاع: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
