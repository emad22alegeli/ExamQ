package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Exam::class, QuestionBankItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExamDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun questionBankDao(): QuestionBankDao

    companion object {
        @Volatile
        private var INSTANCE: ExamDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ExamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExamDatabase::class.java,
                    "iraqi_exams_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.examDao(), database.questionBankDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(examDao: ExamDao, bankDao: QuestionBankDao) {
            // Seed 1: Iraqi Physics & Science Exam (الثالث المتوسط)
            val physicsExam = Exam(
                title = "امتحان نصف السنة - مادة الفيزياء",
                subject = "الفيزياء",
                grade = "الثالث المتوسط",
                totalTargetScore = 100.0,
                header = ExamHeader(
                    ministry = "جمهورية العراق - وزارة التربية",
                    directorate = "المديرية العامة لتربية بغداد / الرصافة الأولى",
                    schoolName = "متوسطة الرافدين للبنين",
                    examTitle = "أسئلة امتحان نصف السنة للعام الدراسي",
                    academicYear = "2024 - 2025 م",
                    subject = "الفيزياء",
                    grade = "الثالث المتوسط",
                    examDate = "2025/01/21",
                    duration = "ساعتان",
                    generalInstruction = "ملاحظة: الإجابة عن خمسة أسئلة فقط ولكل سؤال (20) درجة",
                    showStudentInfoRow = true,
                    teacherName = "أ. حيدر الكرخي",
                    closingWish = "مع تمنياتي لكم بالتفوق والنجاح الباهر",
                    examGroup = "A"
                ),
                sections = listOf(
                    ExamSection(
                        title = "السؤال الأول (20 درجة):",
                        instruction = "أ) ما المقصود باثنين فقط مما يأتي:",
                        type = QuestionType.DEFINITIONS,
                        sectionScore = 20.0,
                        subQuestions = listOf(
                            SubQuestion(label = "1-", text = "قانون كولوم (Coulomb's Law)", score = 5.0, modelAnswer = "القوة الكهربائية المتبادلة بين شحنتين كهربائيتين نقطيتين ساكنتين تتناسب طردياً مع حاصل ضرب مقداريهما وعكسياً مع مربع البعد بينهما."),
                            SubQuestion(label = "2-", text = "المجال الكهربائي المنتظم", score = 5.0, modelAnswer = "هو المجال الذي تكون خطوطه متوازية مع بعضها وتبعد بأبعاد متساوية ويكون ثابتاً في المقدار والاتجاه عند جميع نقاطه."),
                            SubQuestion(label = "3-", text = "الأمبير (Ampere)", score = 5.0, modelAnswer = "تدفق شحنة كهربائية مقدارها كولوم واحد في مقطع موصل خلال ثانية واحدة.")
                        )
                    ),
                    ExamSection(
                        title = "السؤال الثاني (20 درجة):",
                        instruction = "اختر الإجابة الصحيحة بين الأقواس لما يأتي:",
                        type = QuestionType.MULTIPLE_CHOICE,
                        sectionScore = 20.0,
                        subQuestions = listOf(
                            SubQuestion(label = "1-", text = "وحدة قياس الشحنة الكهربائية هي:", options = listOf("الكولوم (C)", "الأمبير (A)", "الفولت (V)", "الجول (J)"), score = 5.0, modelAnswer = "الكولوم (C)"),
                            SubQuestion(label = "2-", text = "تزداد سعة المتسعة ذات الصفيحتين المتوازيتين عند:", options = listOf("زيادة المساحة المتقابلة", "زيادة البعد بينهما", "تقليل العازل", "تقليل الشحنة"), score = 5.0, modelAnswer = "زيادة المساحة المتقابلة"),
                            SubQuestion(label = "3-", text = "المقاومة الكهربائية لسلك موصل تتناسب طردياً مع:", options = listOf("طول السلك (L)", "مساحة المقطع (A)", "درجة برودة السلك", "كثافة السلك"), score = 5.0, modelAnswer = "طول السلك (L)"),
                            SubQuestion(label = "4-", text = "المولد الكهربائي يحول الطاقة الميكانيكية إلى طاقة:", options = listOf("كهربائية", "حرارية", "ضوئية", "كيميائية"), score = 5.0, modelAnswer = "كهربائية")
                        )
                    ),
                    ExamSection(
                        title = "السؤال الثالث (20 درجة):",
                        instruction = "مسألة حسابية: أجب عما يأتي بالتفصيل مع كتابة القوانين والوحدات:",
                        type = QuestionType.MATH_PROBLEM,
                        sectionScore = 20.0,
                        subQuestions = listOf(
                            SubQuestion(
                                label = "أ-",
                                text = "شحنتان نقطيتان موجبتان مقدارهما q₁ = 3 × 10⁻⁶ C و q₂ = 4 × 10⁻⁶ C، البعد بينهما r = 0.3 m. احسب مقدار قوة التنافر الكهربائية المتبادلة بينهما، علماً أن ثابت كولوم k = 9 × 10⁹ N·m²/C².",
                                score = 10.0,
                                modelAnswer = "F = k · (q₁ · q₂) / r² = (9×10⁹ × 3×10⁻⁶ × 4×10⁻⁶) / (0.3)² = (108×10⁻³) / 0.09 = 1.2 N (قوة تنافر)"
                            ),
                            SubQuestion(
                                label = "ب-",
                                text = "دائرة كهربائية تحتوي على مقاومتين R₁ = 6 Ω و R₂ = 3 Ω مربوطتان على التوازي ومربوطتان عبر فرق جهد V = 12 V. احسب: 1) المقاومة المكافئة Req. 2) التيار الكلي في الدائرة I_total.",
                                score = 10.0,
                                modelAnswer = "1/Req = 1/6 + 1/3 = 3/6 = 1/2 => Req = 2 Ω. I_total = V / Req = 12 / 2 = 6 A."
                            )
                        )
                    ),
                    ExamSection(
                        title = "السؤال الرابع (20 درجة):",
                        instruction = "أ) علل ما يأتي لثلاث فقط:  ب) ارسم مع التأشير:",
                        type = QuestionType.REASONING_COMPARISON,
                        sectionScore = 20.0,
                        subQuestions = listOf(
                            SubQuestion(label = "1-", text = "تجهّز سيارات نقل الوقود بسلاسل حديدية تلامس الأرض أثناء سيرها.", score = 5.0, modelAnswer = "لتفريغ الشحنات الكهربائية الساكنة المتولدة من احتكاك الوقود بجدران الخزان إلى الأرض تفادياً لحدوث شرارة وحريق."),
                            SubQuestion(label = "2-", text = "تنجذب قصاصات الورق الصغيرة إلى المشط المدلوك بالشعر الجاف.", score = 5.0, modelAnswer = "لأن المشط يصبح مشحوناً بشحنات ساكنة نتيجة الدلك فيجذب القصاصات بالحث الكهربائي."),
                            SubQuestion(label = "3-", text = "يفضل ربط الأجهزة الكهربائية في المنازل على التوازي وليس على التوالي.", score = 5.0, modelAnswer = "لكي تعمل جميع الأجهزة على نفس فرق الجهد (220V)، وإذا انطفأ أو تلف أحد الأجهزة لا تنطفئ بقية الأجهزة."),
                            SubQuestion(
                                label = "ب-",
                                text = "ارسم رسماً توضيحياً يبيّن خطوط المجال المغناطيسي حول ساق مغناطيسية مستقيمة مع التأشير على القطبين (N و S).",
                                drawingHeightDp = 150,
                                score = 5.0,
                                modelAnswer = "رسم ساق مغناطيسية مستطيلة بقطبين N و S، وخطوط مقفلة تنبع من القطب الشمالي وتتجه نحو القطب الجنوبي خارج المغناطيس."
                            )
                        )
                    ),
                    ExamSection(
                        title = "السؤال الخامس (20 درجة):",
                        instruction = "وصّل بين مفردات العمود (أ) بما يناسبها من العمود (ب):",
                        type = QuestionType.MATCHING,
                        sectionScore = 20.0,
                        subQuestions = listOf(
                            SubQuestion(
                                label = "1-",
                                text = "طابق بين الجهاز الكهربائي ووظيفته المحددة:",
                                matchingPairs = listOf(
                                    MatchingPair(columnA = "1. جهاز الأميتر (Ammeter)", columnB = "أ. قياس فرق الجهد الكهربائي", correctMatch = "1 -> ج"),
                                    MatchingPair(columnA = "2. جهاز الفولطميتر (Voltmeter)", columnB = "ب. الكشف عن الشحنات الكهربائية ونوعها", correctMatch = "2 -> أ"),
                                    MatchingPair(columnA = "3. الكشاف الكهربائي (Electroscope)", columnB = "ج. قياس مقدار التيار الكهربائي المنساب", correctMatch = "3 -> ب"),
                                    MatchingPair(columnA = "4. البوصلة المغناطيسية", columnB = "د. تحديد اتجاه خطوط المجال المغناطيسي", correctMatch = "4 -> د")
                                ),
                                score = 20.0,
                                modelAnswer = "1 -> ج | 2 -> أ | 3 -> ب | 4 -> د"
                            )
                        )
                    )
                )
            )
            examDao.insertExam(physicsExam)

            // Seed Question Bank with rich Iraqi Curriculum items
            val initialBank = listOf(
                QuestionBankItem(
                    subject = "اللغة العربية",
                    grade = "السادس الإعدادي",
                    chapter = "أسلوب النفي",
                    difficulty = "متوسط",
                    type = QuestionType.SHORT_ESSAY,
                    subQuestion = SubQuestion(
                        label = "1-",
                        text = "قال الشاعر: 'ولستُ بمستبقٍ أخاً لا تلمّهُ على شَعَثٍ، أيُّ الرجالِ المهذّبُ'. دلّ على معمولي (ليس)، وبيّن نوع خبرها.",
                        score = 5.0,
                        modelAnswer = "اسم ليس: الضمير المتصل (التاء). خبر ليس: (بمستبقٍ) مجرور لفظاً منصوب محلاً بحرف الجر الزائد الباء."
                    )
                ),
                QuestionBankItem(
                    subject = "الكيمياء",
                    grade = "الثالث المتوسط",
                    chapter = "الجدول الدوري والطاقة",
                    difficulty = "سهل",
                    type = QuestionType.DEFINITIONS,
                    subQuestion = SubQuestion(
                        label = "1-",
                        text = "طاقة التأين (Ionization Energy)",
                        score = 5.0,
                        modelAnswer = "مقدار الطاقة اللازمة لانتزاع إلكترون واحد من مستوى الطاقة الخارجي لذرة عنصر معينة متعادلة الشحنة في حالتها الغازية."
                    )
                ),
                QuestionBankItem(
                    subject = "الرياضيات",
                    grade = "الثالث المتوسط",
                    chapter = "المعادلات الخطية والتربيعية",
                    difficulty = "صعب",
                    type = QuestionType.MATH_PROBLEM,
                    subQuestion = SubQuestion(
                        label = "1-",
                        text = "حل المعادلة الآتية في R باستخدام المقدار المميز (الدستور): 2x² - 3x - 5 = 0.",
                        score = 10.0,
                        modelAnswer = "a = 2, b = -3, c = -5. Δ = b² - 4ac = (-3)² - 4(2)(-5) = 9 + 40 = 49. x = (-b ± √Δ) / 2a = (3 ± 7) / 4 => x₁ = 10/4 = 2.5, x₂ = -4/4 = -1. مجموعة الحل S = {2.5, -1}."
                    )
                ),
                QuestionBankItem(
                    subject = "الأحياء",
                    grade = "الثالث المتوسط",
                    chapter = "الجهاز العضلي والعصبي",
                    difficulty = "متوسط",
                    type = QuestionType.REASONING_COMPARISON,
                    subQuestion = SubQuestion(
                        label = "1-",
                        text = "قارن بين العضلات الهيكلية والعضلات الملساء من حيث: (الموقع، شكل الخلية، نوع التحكم الإرادي).",
                        score = 8.0,
                        modelAnswer = "الهيكلية: ترتبط بالهيكل العظمي، أسطوانية مخططة عديدة الأنوية، إرادية. الملساء: في جدران القناة الهضمية والأوعية، مغزلية غير مخططة أحادية النواة، لا إرادية."
                    )
                ),
                QuestionBankItem(
                    subject = "اللغة الإنجليزية",
                    grade = "الثالث المتوسط",
                    chapter = "Unit 1 - Descriptive Language",
                    difficulty = "متوسط",
                    type = QuestionType.ENGLISH_LTR,
                    subQuestion = SubQuestion(
                        label = "1-",
                        text = "Complete the sentence with the correct relative pronoun (who / which / where): 'The man ______ helped me yesterday was very polite.'",
                        options = listOf("who", "which", "where", "whose"),
                        isLtr = true,
                        score = 4.0,
                        modelAnswer = "who"
                    )
                )
            )
            bankDao.insertQuestions(initialBank)
        }
    }
}
