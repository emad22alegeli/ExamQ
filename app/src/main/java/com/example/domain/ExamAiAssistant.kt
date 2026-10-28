package com.example.domain

import com.example.data.model.ExamSection
import com.example.data.model.MatchingPair
import com.example.data.model.QuestionType
import com.example.data.model.SubQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExamAiAssistant {

    suspend fun generateQuestionsForSubject(
        subject: String,
        grade: String,
        lessonTopic: String,
        requestedType: QuestionType? = null
    ): List<ExamSection> = withContext(Dispatchers.Default) {
        // Build rich, curriculum-aligned questions for the specified subject and topic
        when (subject) {
            "الفيزياء" -> generatePhysicsSections(topic = lessonTopic, grade = grade)
            "الكيمياء" -> generateChemistrySections(topic = lessonTopic, grade = grade)
            "الأحياء" -> generateBiologySections(topic = lessonTopic, grade = grade)
            "الرياضيات" -> generateMathSections(topic = lessonTopic, grade = grade)
            "اللغة العربية" -> generateArabicSections(topic = lessonTopic, grade = grade)
            "اللغة الإنجليزية" -> generateEnglishSections(topic = lessonTopic, grade = grade)
            "التربية الإسلامية" -> generateIslamicSections(topic = lessonTopic, grade = grade)
            "التاريخ", "الجغرافيا", "التربية الوطنية والاجتماعيات" -> generateSocialSections(topic = lessonTopic, grade = grade, subject = subject)
            "الحاسوب" -> generateComputerSections(topic = lessonTopic, grade = grade)
            else -> generateGenericSections(subject = subject, topic = lessonTopic, grade = grade)
        }
    }

    private fun generatePhysicsSections(topic: String, grade: String): List<ExamSection> {
        val topicDisplay = if (topic.isBlank()) "الكهربائية والمغناطيسية" else topic
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "عرّف اثنين فقط مما يأتي بأسلوب علمي دقيق:",
                type = QuestionType.DEFINITIONS,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "التيار الكهربائي المستمر (DC)", score = 5.0, modelAnswer = "تيار ثابت الاتجاه والمقدار ينساب من القطب الموجب إلى السالب ومصدره البطاريات."),
                    SubQuestion(label = "2-", text = "المقاومة النوعية (Resistivity)", score = 5.0, modelAnswer = "مقاومة موصل طوله متر واحد ومساحة مقطعه العرضي متر مربع واحد عند درجة حرارة معينة."),
                    SubQuestion(label = "3-", text = "القدرة الكهربائية (Electric Power)", score = 5.0, modelAnswer = "مقدار الطاقة الكهربائية التي يستهلكها أو يستثمرها الجهاز في وحدة الزمن وتقاس بالواط.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "علل ما يأتي لثلاث فقط من موضوع ($topicDisplay):",
                type = QuestionType.REASONING_COMPARISON,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "يؤرّض الغلاف المعدني للأجهزة الكهربائية المنزلية بواسطة القابس الثلاثي.", score = 5.0, modelAnswer = "لحماية مستخدم الجهاز من الصعقة الكهربائية عند حدوث خلل أو تماس بين السلك الحي والغلاف."),
                    SubQuestion(label = "2-", text = "تفضّل المتسعات متغيرة السعة في دوائر التنغيم بالراديو.", score = 5.0, modelAnswer = "لإمكانية تغيير سعتها بسهولة واختيار التردد المطلوب للبث بدقة."),
                    SubQuestion(label = "3-", text = "لا تتقاطع خطوط المجال المغناطيسي مطلقاً.", score = 5.0, modelAnswer = "لأنه لو تقاطعت لكان لنقطة التقاطع أكثر من اتجاه للمجال، وهو مستحيل فيزيائياً.")
                )
            ),
            ExamSection(
                title = "السؤال الثالث (20 درجة):",
                instruction = "مسألة حسابية: أجب عما يأتي مع كتابة القوانين المعنية:",
                type = QuestionType.MATH_PROBLEM,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(
                        label = "أ-",
                        text = "مصباح كهربائي مكتوب عليه 220V و 100W، احسب: 1) التيار المنساب في المصباح. 2) المقاومة الكهربائية لسلك المصباح.",
                        score = 10.0,
                        modelAnswer = "P = V × I => I = P / V = 100 / 220 = 0.45 A. R = V² / P = (220)² / 100 = 484 Ω."
                    ),
                    SubQuestion(
                        label = "ب-",
                        text = "ارسم الدائرة الكهربائية المستعملة لقياس مقاومة مجهولة بطريقة الأميتر والفولطميتر موضحاً أجهزة القياس ومفتاح الدائرة.",
                        drawingHeightDp = 130,
                        score = 10.0,
                        modelAnswer = "رسم مصدر طاقة (بطارية) ومفتاح، مع أميتر على التوالي ومقاومة R مجهولة، وفولطميتر مربوط على التوازي مع المقاومة."
                    )
                )
            )
        )
    }

    private fun generateChemistrySections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "أكمل الفراغات الآتية بما يناسبها علمياً:",
                type = QuestionType.FILL_IN_BLANKS,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "العنصر الذي يقع في الزمرة الأولى والدورة الثانية هو عنصر ............. ورمزه الكيميائي .............", score = 5.0, modelAnswer = "الليثيوم (Lithium) ورمزه Li"),
                    SubQuestion(label = "2-", text = "الصيغة الكيميائية لغاز ثنائي أكسيد الكربون هي ............. ولغاز الميثان .............", score = 5.0, modelAnswer = "CO₂ والميثان CH₄"),
                    SubQuestion(label = "3-", text = "يسمّى التفاعل الذي يحرر طاقة حرارية تفاعلاً ............. للحرارة.", score = 5.0, modelAnswer = "باعثاً"),
                    SubQuestion(label = "4-", text = "عدد الإلكترونات في الغلاف الخارجي لذرة الأكسجين ₈O هو ............. إلكترونات.", score = 5.0, modelAnswer = "6")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "اكتب المعادلات الكيميائية المتوازنة للتحضيرات الآتية:",
                type = QuestionType.MATH_PROBLEM,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "أ-", text = "تفاعل الصوديوم الصلب Na مع غاز الكلور Cl₂ لإنتاج كلوريد الصوديوم.", score = 10.0, modelAnswer = "2Na(s) + Cl₂(g) → 2NaCl(s)"),
                    SubQuestion(label = "ب-", text = "تحضير غاز النيتروجين N₂ مختبرياً مع ذكر شروط التفاعل والجهاز المستعمل.", score = 10.0, modelAnswer = "NH₄Cl + NaNO₂ → (Heat) NaCl + 2H₂O + N₂↑")
                )
            )
        )
    }

    private fun generateMathSections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "أجب عن فرعين فقط مما يأتي بالتفصيل:",
                type = QuestionType.MATH_PROBLEM,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "أ-", text = "جد مجموعة حل النظام الآتي في R باستخدام طريقة الحذف:\n3x + 2y = 12\n2x - y = 1", score = 10.0, modelAnswer = "بضرب المعادلة الثانية في 2: 4x - 2y = 2. بالجمع: 7x = 14 => x = 2. بالتعويض: y = 2(2) - 1 = 3. مجموعة الحل {(2, 3)}"),
                    SubQuestion(label = "ب-", text = "بسّط المقدار الجبري الآتي لأبسط صورة:\n(x² - 9) / (x² + 6x + 9) ÷ (2x - 6) / (x + 3)", score = 10.0, modelAnswer = "((x-3)(x+3)) / (x+3)² × (x+3) / 2(x-3) = 1/2"),
                    SubQuestion(label = "ج-", text = "مثلث قائم الزاوية طولا ضلعيه القائمين 6 cm و 8 cm. جد طول الوتر ومساحة المثلث.", score = 10.0, modelAnswer = "الوتر = √(6² + 8²) = √(36 + 64) = 10 cm. المساحة = ½ × 6 × 8 = 24 cm².")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "اختر الإجابة الصحيحة من بين الأقواس:",
                type = QuestionType.MULTIPLE_CHOICE,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "ميل المستقيم المار بالنقطتين (2, 3) و (4, 7) يساوي:", options = listOf("2", "4", "½", "-2"), score = 5.0, modelAnswer = "2"),
                    SubQuestion(label = "2-", text = "المتتابعة الحسابية التي حدها الأول 3 وأساسها 4، حدها الخامس هو:", options = listOf("19", "15", "23", "27"), score = 5.0, modelAnswer = "19"),
                    SubQuestion(label = "3-", text = "مجموعة حل المعادلة x² - 25 = 0 في R هي:", options = listOf("{5, -5}", "{5}", "{-5}", "∅"), score = 5.0, modelAnswer = "{5, -5}"),
                    SubQuestion(label = "4-", text = "قيمة المقدار sin(30°) + cos(60°) تساوي:", options = listOf("1", "0", "√3", "½"), score = 5.0, modelAnswer = "1")
                )
            )
        )
    }

    private fun generateArabicSections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (قواعد اللغة العربية - 25 درجة):",
                instruction = "أ) اقرأ النص الآتي بتأنٍ ثم أجب عن المطاليب:\n'إنَّ العِلمَ نورٌ يَهدي الإنسانَ في دُروبِ الحياةِ، فما خابَ مَن تَمَسَّكَ بهِ، ويا طالباً اجتهِدْ لِتبنيَ وطَنَكَ.'",
                type = QuestionType.SHORT_ESSAY,
                sectionScore = 25.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "استخرج اسم (إنَّ) وخبرها وبيّن علامة إعراب كلٍّ منهما.", score = 6.0, modelAnswer = "اسم إن: (العلمَ) منصوب بالفتحة الظاهرة. خبر إن: (نورٌ) مرفوع بالضمة الظاهرة."),
                    SubQuestion(label = "2-", text = "ما نوع (ما) في قوله: (فما خابَ)، وما الزمن الذي نفته؟", score = 6.0, modelAnswer = "(ما) نافية غير عاملة، نفت الفعل في الزمن الماضي نفياً شاملاً."),
                    SubQuestion(label = "3-", text = "دلّ على المنادى في النص وبيّن نوعه وحكمه الإعرابي.", score = 6.0, modelAnswer = "المنادى: (طالباً)، نكرة غير مقصودة، معرب منصوب وعلامة نصبه الفتحة."),
                    SubQuestion(label = "4-", text = "أعرب ما تحته خط إعراباً مفصلاً: (لِتبنيَ).", score = 7.0, modelAnswer = "اللام: لام التعليل حرف نصب. تبنيَ: فعل مضارع منصوب بلام التعليل وعلامة نصبه الفتحة الظاهرة على الياء، والفاعل ضمير مستتر تقديره أنت.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (الأدب والنصوص - 15 درجة):",
                instruction = "أجب عما يأتي لفرعين فقط:",
                type = QuestionType.SHORT_ESSAY,
                sectionScore = 15.0,
                subQuestions = listOf(
                    SubQuestion(label = "أ-", text = "من هو رائد حركة الشعر الحر في العراق؟ اذكر اسم قصيدة مشهورة له واكتب ثلاثة أبيات مضبوطة بالشكل.", score = 7.5, modelAnswer = "الشاعر بدر شاكر السياب (أو نازك الملائكة). قصيدة 'أنشودة المطر': عيناكِ غابتا نخيلٍ ساعةَ السَّحَرْ..."),
                    SubQuestion(label = "ب-", text = "ما هي خصائص أسلوب الشاعر محمد مهدي الجواهري؟ ولماذا لُقّب بشاعر العرب الأكبر؟", score = 7.5, modelAnswer = "قوة السبك اللغوي، جزالة الألفاظ، النَفَس الثوري والأصالة. ولُقّب بذلك لمكانته الشعرية الرفيعة واستمراره على عمود الشعر العربي الأصيل.")
                )
            )
        )
    }

    private fun generateEnglishSections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "Question 1: Reading Comprehension (20 Marks)",
                instruction = "Read the following passage carefully, then answer the questions:",
                type = QuestionType.READING_COMPREHENSION,
                sectionScore = 20.0,
                isLtr = true,
                subQuestions = listOf(
                    SubQuestion(
                        label = "Passage:",
                        text = "Ibn al-Haytham was a great Muslim scientist born in Basra, Iraq in 965 AD. He is known as the father of modern optics. He wrote many books, the most famous of which is 'Kitab al-Manazir' (Book of Optics). He proved that light travels in straight lines and that we see objects because light reflects from them into our eyes.",
                        isLtr = true,
                        score = 0.0
                    ),
                    SubQuestion(label = "1-", text = "Where and when was Ibn al-Haytham born?", isLtr = true, score = 5.0, modelAnswer = "He was born in Basra, Iraq in 965 AD."),
                    SubQuestion(label = "2-", text = "What is his most famous scientific book?", isLtr = true, score = 5.0, modelAnswer = "Kitab al-Manazir (Book of Optics)."),
                    SubQuestion(label = "3-", text = "How did he explain the mechanism of vision?", isLtr = true, score = 5.0, modelAnswer = "Light reflects from objects into our eyes."),
                    SubQuestion(label = "4-", text = "Choose True or False: Ibn al-Haytham believed light travels in curved lines.", options = listOf("True", "False"), isLtr = true, score = 5.0, modelAnswer = "False (he proved it travels in straight lines).")
                )
            ),
            ExamSection(
                title = "Question 2: Grammar & Vocabulary (20 Marks)",
                instruction = "Do as required between brackets for each sentence:",
                type = QuestionType.ENGLISH_LTR,
                sectionScore = 20.0,
                isLtr = true,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "If Ali studies hard, he (pass) the final exam. [Correct the verb]", isLtr = true, score = 5.0, modelAnswer = "will pass (First Conditional)"),
                    SubQuestion(label = "2-", text = "They have lived in Baghdad ______ ten years. [Choose: since / for / ago]", options = listOf("for", "since", "ago"), isLtr = true, score = 5.0, modelAnswer = "for"),
                    SubQuestion(label = "3-", text = "Someone stole my car yesterday. [Rewrite in Passive Voice]", isLtr = true, score = 5.0, modelAnswer = "My car was stolen yesterday."),
                    SubQuestion(label = "4-", text = "She is very interested ______ computer science. [Preposition: in / on / at / with]", options = listOf("in", "on", "at", "with"), isLtr = true, score = 5.0, modelAnswer = "in")
                )
            )
        )
    }

    private fun generateIslamicSections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (القرآن الكريم والحديث النبوي - 25 درجة):",
                instruction = "أ) اكتب ما تحفظه من سورة (الرحمن) من قوله تعالى: (الرَّحْمَنُ * عَلَّمَ الْقُرْآنَ...) إلى قوله تعالى: (أَلَّا تَطْغَوْا فِي الْمِيزَانِ).",
                type = QuestionType.SHORT_ESSAY,
                sectionScore = 25.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "اكتب الآيات الكريمة مضبوطة بالشكل والحركات.", score = 15.0, modelAnswer = "الرَّحْمَٰنُ (1) عَلَّمَ الْقُرْآنَ (2) خَلَقَ الْإِنسَانَ (3) عَلَّمَهُ الْبَيَانَ (4) الشَّمْسُ وَالْقَمَرُ بِحُسْبَانٍ (5) وَالنَّجْمُ وَالشَّجَرُ يَسْجُدَانِ (6) وَالسَّمَاءَ رَفَعَهَا وَوَضَعَ الْمِيزَانَ (7) أَلَّا تَطْغَوْا فِي الْمِيزَانِ (8)"),
                    SubQuestion(label = "2-", text = "اكتب حديثاً نبوياً شريفاً في (بر الوالدين) أو (طلب العلم) مع ذكر أهم ما يرشد إليه الحديث.", score = 10.0, modelAnswer = "قال رسول الله (صلى الله عليه وسلم): 'من سلك طريقاً يلتمس فيه علماً سهّل الله له به طريقاً إلى الجنة'. يرشد إلى فضل طلب العلم والسعي في تحصيله لخدمة المجتمع.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (العقيدة والفقه الإسلامي - 15 درجة):",
                instruction = "ما المقصود باثنين فقط مما يأتي:",
                type = QuestionType.DEFINITIONS,
                sectionScore = 15.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "اليوم الآخر والإيمان بالبعث والجزاء", score = 7.5, modelAnswer = "التصديق الجازم بأن الله يبعث الناس من قبورهم بعد فناء الدنيا للحساب والجزاء العادل إما جنة أو نار."),
                    SubQuestion(label = "2-", text = "الخُمس ومصارفه الشرعية", score = 7.5, modelAnswer = "حق مالي شرعي يخرج من فاضل المؤونة والأرباح بعد سنة، ويوزع على سهم الإمام وسهم السادة الفقراء والأيتام."),
                    SubQuestion(label = "3-", text = "صلاة الآيات ووقت وجوبها", score = 7.5, modelAnswer = "صلاة واجبة تؤدى عند حدوث كسوف الشمس أو خسوف القمر أو الزلازل والظواهر المخيفة.")
                )
            )
        )
    }

    private fun generateBiologySections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "أ) ما موقع ووظيفة كل مما يأتي لثلاث فقط:",
                type = QuestionType.REASONING_COMPARISON,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "لسان المزمار (Epiglottis)", score = 5.0, modelAnswer = "الموقع: أعلى الحنجرة. الوظيفة: يغلق فتحة الحنجرة أثناء بلع الطعام لمنع دخوله إلى القصبة الهوائية."),
                    SubQuestion(label = "2-", text = "الصمام ثلاثي الشرفات في القلب", score = 5.0, modelAnswer = "الموقع: بين الأذين الأيمن والبطين الأيمن. الوظيفة: يسمح بمرور الدم من الأذين إلى البطين ويمنع رجوعه بالاتجاه المعاكس."),
                    SubQuestion(label = "3-", text = "الميولين (الغمد النخاعيني) للخلايا العصبية", score = 5.0, modelAnswer = "الموقع: يغلف محور الخلية العصبية. الوظيفة: عزل المحور العصبي وتسريع انتقال السيلات العصبية.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "ارسم مع التأشير الكامل على الأجزاء:",
                type = QuestionType.DRAWING,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(
                        label = "1-",
                        text = "ارسم رسماً تخطيطياً يوضح تركيب الخلية الحيوانية النموذجية مع التأشير على (الغشاء البلازمي، النواة، المايتوكوندريا، جهاز كولجي).",
                        drawingHeightDp = 160,
                        score = 20.0,
                        modelAnswer = "رسم بيضوي للخلية محاط بغشاء، مع نواة مركزية ونوية، شبكة بلازمية داخلية، وأجسام كولجي ومايتوكوندريا."
                    )
                )
            )
        )
    }

    private fun generateSocialSections(topic: String, grade: String, subject: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "عرّف أربعة فقط مما يأتي:",
                type = QuestionType.DEFINITIONS,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "حضارة وادي الرافدين", score = 5.0, modelAnswer = "من أقدم وأعرق الحضارات الإنسانية نشأت بين نهري دجلة والفرات وقدمت للعالم الكتابة المسمارية والقوانين والشرائع."),
                    SubQuestion(label = "2-", text = "شريعة حمورابي", score = 5.0, modelAnswer = "أقدم القوانين المدونة في التاريخ وضعها الملك البابلي حمورابي وتضم 282 مادة نقشت على مسلة حجرية سوداء."),
                    SubQuestion(label = "3-", text = "شط العرب", score = 5.0, modelAnswer = "مجرى مائي في جنوب العراق يتكون من التقاء نهري دجلة والفرات عند مدينة القرنة ويصب في الخليج العربي."),
                    SubQuestion(label = "4-", text = "الهضبة الغربية في العراق", score = 5.0, modelAnswer = "جزء من الهضبة العربية تشكل أكثر من نصف مساحة العراق تمتد غرب وجنوب غرب البلاد وتتميز بالطابع الصحراوي.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "ضع علامة (✓) أمام العبارة الصحيحة وعلامة (✗) أمام العبارة الخاطئة مع تصحيح الخطأ إن وجد:",
                type = QuestionType.TRUE_FALSE,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "يعد سهل راوندوز من أشهر السهول الرسوبية الخصبة في جنوب العراق.", score = 5.0, modelAnswer = "✗ خطأ، يقع في المنطقة الجبلية في شمال العراق."),
                    SubQuestion(label = "2-", text = "تقع بحيرة الثرثار بين نهري دجلة والفرات إلى الشمال من مدينة سامراء.", score = 5.0, modelAnswer = "✓ صحيحة."),
                    SubQuestion(label = "3-", text = "أعلى قمة جبلية في العراق هي قمة هلكرد في سلسلة جبال حصاروست.", score = 5.0, modelAnswer = "✓ صحيحة (ارتفاعها نحو 3611 متراً)."),
                    SubQuestion(label = "4-", text = "أول عاصمة للدولة العباسية كانت مدينة بغداد.", score = 5.0, modelAnswer = "✗ خطأ، أول عاصمة كانت الكوفة ثم الهاشمية في الأنبار ثم بنى المنصور بغداد.")
                )
            )
        )
    }

    private fun generateComputerSections(topic: String, grade: String): List<ExamSection> {
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "اختر الإجابة الصحيحة لكل من العبارات الآتية:",
                type = QuestionType.MULTIPLE_CHOICE,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "وحدة المعالجة المركزية (CPU) تتكون من وحدتين أساسيتين هما وحدة الحساب والمنطق و:", options = listOf("وحدة التحكم (Control Unit)", "الذاكرة العشوائية (RAM)", "القرص الصلب (Hard Disk)", "لوحة المفاتيح"), score = 5.0, modelAnswer = "وحدة التحكم (Control Unit)"),
                    SubQuestion(label = "2-", text = "ذاكرة القراءة فقط (ROM) تتميز بأنها:", options = listOf("تحتفظ ببياناتها عند انقطاع الكهرباء", "تفرغ بياناتها فور انقطاع التيار", "ذاكرة كتابة سريعة", "مخصصة للمستخدم"), score = 5.0, modelAnswer = "تحتفظ ببياناتها عند انقطاع الكهرباء"),
                    SubQuestion(label = "3-", text = "العدد الثنائي (101)₂ يكافئ في النظام العشري العدد:", options = listOf("5", "6", "7", "3"), score = 5.0, modelAnswer = "5 (1×1 + 0×2 + 1×4 = 5)"),
                    SubQuestion(label = "4-", text = "أي من الآتي يعتبر برنامجاً تطبيقياً وليس نظام تشغيل:", options = listOf("معالج النصوص Word", "Windows 11", "Linux Ubuntu", "Android"), score = 5.0, modelAnswer = "معالج النصوص Word")
                )
            )
        )
    }

    private fun generateGenericSections(subject: String, topic: String, grade: String): List<ExamSection> {
        val topicName = if (topic.isNotBlank()) topic else "المفاهيم العامة لمادة $subject"
        return listOf(
            ExamSection(
                title = "السؤال الأول (20 درجة):",
                instruction = "ما المقصود بالمفاهيم الآتية في مادة $subject:",
                type = QuestionType.DEFINITIONS,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "المفهوم الأساسي الأول من موضوع ($topicName)", score = 10.0, modelAnswer = "تعريف علمي دقيق للمفهوم."),
                    SubQuestion(label = "2-", text = "المفهوم الأساسي الثاني وأهميته العملية والتطبيقية", score = 10.0, modelAnswer = "بيان الأهمية والدور الرئيسي.")
                )
            ),
            ExamSection(
                title = "السؤال الثاني (20 درجة):",
                instruction = "ضع علامة (✓) أو (✗) مع تصحيح الخطأ إن وجد:",
                type = QuestionType.TRUE_FALSE,
                sectionScore = 20.0,
                subQuestions = listOf(
                    SubQuestion(label = "1-", text = "تعتبر القواعد الأساسية لموضوع ($topicName) متوافقة مع المبادئ العامة.", score = 10.0, modelAnswer = "✓ صحيحة."),
                    SubQuestion(label = "2-", text = "النتائج المشتقة من هذه النظرية تتنافى مع التطبيقات الحديثة.", score = 10.0, modelAnswer = "✗ خطأ، بل تدعم التطبيقات وتعزز كفاءتها.")
                )
            )
        )
    }
}
