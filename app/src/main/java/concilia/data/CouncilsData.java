package concilia.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AUTO-GENERATED from the Figma export (src/data/councils.ts).
 * All 21 councils and every lesson / question / explanation are carried over verbatim.
 * Edit freely - this is plain Java data now.
 */
public final class CouncilsData {

    public static final List<Council> COUNCILS;
    public static final List<Era> ERAS;

    static {
        List<Council> l = new ArrayList<>();
        l.add(c1());
        l.add(c2());
        l.add(c3());
        l.add(c4());
        l.add(c5());
        l.add(c6());
        l.add(c7());
        l.add(c8());
        l.add(c9());
        l.add(c10());
        l.add(c11());
        l.add(c12());
        l.add(c13());
        l.add(c14());
        l.add(c15());
        l.add(c16());
        l.add(c17());
        l.add(c18());
        l.add(c19());
        l.add(c20());
        l.add(c21());
        COUNCILS = Collections.unmodifiableList(l);

        List<Era> e = new ArrayList<>();
        e.add(new Era("Patristic Age", 0xFF8B6914, "325–787 AD", new int[]{1, 2, 3, 4, 5, 6, 7}));
        e.add(new Era("Byzantine Age", 0xFF4A3080, "869–870 AD", new int[]{8}));
        e.add(new Era("Medieval Church", 0xFF1A5C3A, "1123–1274 AD", new int[]{9, 10, 11, 12, 13, 14}));
        e.add(new Era("Late Medieval", 0xFF7A1C1C, "1311–1449 AD", new int[]{15, 16, 17}));
        e.add(new Era("Reformation Era", 0xFF1A3A5C, "1512–1563 AD", new int[]{18, 19}));
        e.add(new Era("Modern Church", 0xFF2C3E50, "1869–1965 AD", new int[]{20, 21}));
        ERAS = Collections.unmodifiableList(e);
    }

    private CouncilsData() {}

    /** The era a council belongs to (matches the Figma ERAS table). */
    public static Era eraFor(Council c) {
        for (Era e : ERAS) {
            for (int n : e.councils) if (n == c.number) return e;
        }
        return ERAS.get(0);
    }

    public static Council byId(int id) {
        for (Council c : COUNCILS) if (c.id == id) return c;
        return null;
    }

    public static List<Lesson> lessonsFor(android.content.Context ctx, Council co) {
        if (co == null) return java.util.Collections.emptyList();
        List<Lesson> fromJson = CouncilJsonLoader.loadLessons(ctx, co.id);
        return fromJson != null ? fromJson : co.lessons;
    }

    private static Council c1() {
        return new Council(
            1, 1,
            "First Council of Nicaea",
            "325 AD",
            "Nicaea, Bithynia",
            "Patristic Age", 0xFF8B6914,
            "Arianism",
            "Christ is consubstantial with the Father (homoousios)",
            "St. Athanasius", "Deacon of Alexandria",
            "The first and foundational council, convened by Emperor Constantine I, addressed the Arian controversy which denied the full divinity of Christ. Over 300 bishops gathered to define the nature of Christ and produced the original Nicene Creed.",
            "We believe in one Lord Jesus Christ, the only Son of God, eternally begotten of the Father.",
            "Nicene Creed, 325 AD",
            Arrays.asList(
                Lesson.info("1-1", "The Arian Crisis",
                    "Arius, a priest from Alexandria, taught that the Son of God was a created being — the first of God's creations, but not co-eternal with the Father. His slogan was \"There was a time when He was not.\" This spread rapidly through the empire."),
                Lesson.quiz("1-2", "Test Your Knowledge",
                    "What key Greek term did the Council of Nicaea use to describe the relationship between the Son and the Father?",
                    new String[]{"Homoiousios (similar substance)", "Homoousios (same substance)", "Heteroousios (different substance)", "Anomoios (unlike)"},
                    1,
                    "Homoousios — meaning \"of the same substance\" — was the term that defined Nicene orthodoxy. Arius had taught homoiousios or heteroousios, denying full equality with the Father."),
                Lesson.quiz("1-3", "The Council's Emperor",
                    "Which Roman Emperor convened the First Council of Nicaea?",
                    new String[]{"Diocletian", "Theodosius I", "Constantine I", "Julian"},
                    2,
                    "Constantine I convened the council in 325 AD at his palace in Nicaea. Though not yet baptized, he presided over the opening and sought unity for his empire.")
            ));
    }

    private static Council c2() {
        return new Council(
            2, 2,
            "First Council of Constantinople",
            "381 AD",
            "Constantinople",
            "Patristic Age", 0xFF8B6914,
            "Pneumatomachianism",
            "The Holy Spirit is fully God, equal to Father and Son",
            "St. Gregory of Nazianzus", "Bishop of Constantinople",
            "Called by Emperor Theodosius I, this council expanded the Nicene Creed to affirm the full divinity of the Holy Spirit against the Pneumatomachians (Spirit-fighters) who denied the Spirit's divine status.",
            "We believe in the Holy Spirit, the Lord, the Giver of Life, who proceeds from the Father.",
            "Niceno-Constantinopolitan Creed, 381 AD",
            Arrays.asList(
                Lesson.info("2-1", "The Spirit-Fighters",
                    "After Nicaea settled the question of the Son, a new controversy arose over the Holy Spirit. The Pneumatomachians, led by Macedonius, acknowledged Christ's divinity but denied that the Spirit was truly God — treating the Spirit as a superior angel or created force."),
                Lesson.quiz("2-2", "The Spirit's Procession",
                    "From whom does the Niceno-Constantinopolitan Creed say the Holy Spirit proceeds?",
                    new String[]{"From the Son alone", "From the Father and the Son", "From the Father alone", "From the Church"},
                    2,
                    "The original 381 creed states the Spirit proceeds \"from the Father.\" The Western addition of \"and the Son\" (Filioque) came later and was a major factor in the Great Schism of 1054.")
            ));
    }

    private static Council c3() {
        return new Council(
            3, 3,
            "Council of Ephesus",
            "431 AD",
            "Ephesus, Asia Minor",
            "Patristic Age", 0xFF8B6914,
            "Nestorianism",
            "Mary is Theotokos (God-bearer); Christ has one unified person",
            "St. Cyril of Alexandria", "Archbishop of Alexandria",
            "Convened to address Nestorius of Constantinople, who objected to calling Mary \"Theotokos\" (God-bearer). The council affirmed that Christ is one person and thus Mary rightly bears the title Mother of God.",
            "We confess that the holy Virgin is Theotokos, because God the Word was incarnate and became man.",
            "Council of Ephesus, 431 AD",
            Arrays.asList(
                Lesson.info("3-1", "The Nestorian Controversy",
                    "Nestorius, the Archbishop of Constantinople, preferred to call Mary \"Christotokos\" (Christ-bearer) rather than \"Theotokos\" (God-bearer). His opponents saw this as implying two separate persons in Christ — one human, one divine."),
                Lesson.quiz("3-2", "Theotokos",
                    "What does the Greek title \"Theotokos\" mean?",
                    new String[]{"Holy Virgin", "Christ-bearer", "God-bearer / Mother of God", "Star of the Sea"},
                    2,
                    "\"Theotokos\" combines Theos (God) and tokos (one who bears). Because Christ is one divine person, and Mary bore that person, she is rightly called God-bearer.")
            ));
    }

    private static Council c4() {
        return new Council(
            4, 4,
            "Council of Chalcedon",
            "451 AD",
            "Chalcedon, Bithynia",
            "Patristic Age", 0xFF8B6914,
            "Monophysitism",
            "Christ has two natures — divine and human — in one person",
            "Pope Leo I", "Bishop of Rome",
            "The most theologically significant council, defining Christ as one person in two natures \"without confusion, change, division, or separation.\" The Chalcedonian Definition remains the foundation of Christology for Catholic, Orthodox, and Protestant churches.",
            "Two natures, without confusion, without change, without division, without separation.",
            "Chalcedonian Definition, 451 AD",
            Arrays.asList(
                Lesson.info("4-1", "The Four Adverbs",
                    "The Chalcedonian Definition uses four precise adverbs to define Christ's two natures: without confusion (against mixing), without change (against transformation), without division (against Nestorianism), without separation (against Nestorianism). These four words guard against all major errors simultaneously."),
                Lesson.quiz("4-2", "The Chalcedonian Adverbs",
                    "Which heresy does the phrase \"without division, without separation\" specifically refute?",
                    new String[]{"Monophysitism", "Arianism", "Nestorianism", "Docetism"},
                    2,
                    "\"Without division, without separation\" refutes Nestorianism — the view that Christ's two natures were so distinct they were practically two separate persons.")
            ));
    }

    private static Council c5() {
        return new Council(
            5, 5,
            "Second Council of Constantinople",
            "553 AD",
            "Constantinople",
            "Byzantine Age", 0xFF4A3080,
            "Three Chapters Controversy",
            "Condemned writings of Theodore of Mopsuestia, Theodoret of Cyrrhus, and Ibas of Edessa",
            "Emperor Justinian I", "Byzantine Emperor",
            "Called by Justinian I to heal the division caused by Chalcedon. The council condemned the \"Three Chapters\" — writings of three theologians seen as favorable to Nestorianism — over Pope Vigilius's initial resistance.",
            "If anyone does not anathematize Arius, Eunomius, Macedonius, Apollinaris, Nestorius, Eutyches and Origen... let him be anathema.",
            "Second Council of Constantinople, 553 AD",
            Arrays.asList(
                Lesson.info("5-1", "The Three Chapters",
                    "The \"Three Chapters\" referred to specific writings of three Antiochene theologians that Justinian wanted condemned to reconcile the Monophysites with Chalcedonian orthodoxy. Pope Vigilius was brought to Constantinople under pressure and initially refused to cooperate."),
                Lesson.quiz("5-2", "Political Context",
                    "Which Byzantine emperor pressured Pope Vigilius and ultimately convened the Second Council of Constantinople?",
                    new String[]{"Heraclius", "Justinian I", "Constantine IV", "Theodosius II"},
                    1,
                    "Justinian I (527–565) convened this council. His reign is famous for reconquering much of the Western Empire and commissioning the Hagia Sophia.")
            ));
    }

    private static Council c6() {
        return new Council(
            6, 6,
            "Third Council of Constantinople",
            "680–681 AD",
            "Constantinople",
            "Byzantine Age", 0xFF4A3080,
            "Monothelitism",
            "Christ has two wills — divine and human",
            "St. Maximus the Confessor", "Theologian and Confessor",
            "Settled the Monothelite controversy by affirming that Christ has two wills corresponding to his two natures. Pope Honorius I was posthumously condemned for his ambiguous letter supporting the Monothelite position.",
            "We likewise proclaim in him, according to the teaching of the holy Fathers, two natural wills.",
            "Third Council of Constantinople, 681 AD",
            Arrays.asList(
                Lesson.quiz("6-1", "Monothelitism",
                    "What did the Monothelites believe about Christ's will?",
                    new String[]{"Christ had no human will", "Christ had two wills in conflict", "Christ had only one will (the divine)", "Christ's will was purely human"},
                    2,
                    "Monothelitism (mono = one, thelema = will) taught that despite having two natures, Christ had only one will — the divine. The council rejected this as incompatible with a full human nature.")
            ));
    }

    private static Council c7() {
        return new Council(
            7, 7,
            "Second Council of Nicaea",
            "787 AD",
            "Nicaea, Bithynia",
            "Byzantine Age", 0xFF4A3080,
            "Iconoclasm",
            "Icons are to be venerated, not worshipped; iconoclasm is condemned",
            "Empress Irene", "Byzantine Regent",
            "The last council accepted by both Catholic and Orthodox churches as fully ecumenical. It restored the veneration of icons after the Iconoclast controversy, distinguishing between veneration (proskynesis) and worship (latreia).",
            "We define that the holy icons... are to be given due salutation and honorable reverence.",
            "Second Council of Nicaea, 787 AD",
            Arrays.asList(
                Lesson.info("7-1", "The Iconoclast Crisis",
                    "Beginning with Emperor Leo III in 726, Byzantine emperors ordered the destruction of religious images. Iconoclasts argued that depicting Christ was heretical — either confusing his natures or depicting only his humanity. The council's response drew on the Incarnation: the invisible God became visible in Christ."),
                Lesson.quiz("7-2", "Veneration vs. Worship",
                    "What Greek term did the council use to describe the worship owed to God alone, which is NOT given to icons?",
                    new String[]{"Proskynesis", "Latreia", "Doxa", "Theosis"},
                    1,
                    "\"Latreia\" means the worship or adoration due to God alone. \"Proskynesis\" or \"dulia\" is the veneration given to icons and saints — a qualitatively different honor.")
            ));
    }

    private static Council c8() {
        return new Council(
            8, 8,
            "Fourth Council of Constantinople",
            "869–870 AD",
            "Constantinople",
            "Medieval Church", 0xFF1A5C3A,
            "Photian Schism",
            "Deposed Patriarch Photius; affirmed Pope's authority",
            "Pope Adrian II", "Bishop of Rome",
            "Recognized by Rome as the eighth ecumenical council, this assembly deposed Patriarch Photius of Constantinople and attempted to resolve tensions between Rome and the Eastern church. The Orthodox Church does not recognize it as ecumenical.",
            "The holy Roman Church holds the primacy over all the churches.",
            "Fourth Council of Constantinople, 870 AD",
            Arrays.asList(
                Lesson.quiz("8-1", "A Disputed Council",
                    "Why do Eastern Orthodox churches NOT recognize the Fourth Council of Constantinople as ecumenical?",
                    new String[]{"It was never ratified by any emperor", "It condemned Patriarch Photius, who was later rehabilitated", "It was held outside Constantinople", "It contradicted the Council of Chalcedon"},
                    1,
                    "Photius was later rehabilitated by a subsequent council in Constantinople (879–880), which the Orthodox consider the true Eighth Council. Thus they do not accept the 869–870 council's decisions.")
            ));
    }

    private static Council c9() {
        return new Council(
            9, 9,
            "First Lateran Council",
            "1123 AD",
            "Rome",
            "Medieval Church", 0xFF1A5C3A,
            "Investiture Controversy",
            "Confirmed the Concordat of Worms; clerical celibacy enforced",
            "Pope Calixtus II", "Bishop of Rome",
            "The first ecumenical council held in the West, at the Lateran Palace in Rome. It formally ratified the Concordat of Worms (1122), settling the investiture controversy between popes and emperors over the appointment of bishops.",
            "We absolutely forbid priests, deacons, subdeacons and monks to have concubines or to contract marriages.",
            "First Lateran Council, Canon 3, 1123 AD",
            Arrays.asList(
                Lesson.quiz("9-1", "Investiture",
                    "What was the \"Investiture Controversy\" that the First Lateran Council helped resolve?",
                    new String[]{"Dispute over who could sell indulgences", "Conflict over whether emperors or popes had the right to appoint bishops", "Debate about monastic poverty", "Argument over the date of Easter"},
                    1,
                    "For decades, Holy Roman Emperors and Popes clashed over who had authority to \"invest\" (install) bishops and abbots with their offices. The Concordat of Worms (1122) brokered a compromise.")
            ));
    }

    private static Council c10() {
        return new Council(
            10, 10,
            "Second Lateran Council",
            "1139 AD",
            "Rome",
            "Medieval Church", 0xFF1A5C3A,
            "Antipope Anacletus II",
            "Ended the papal schism; enforced clerical discipline",
            "Pope Innocent II", "Bishop of Rome",
            "Called after Pope Innocent II defeated the schism caused by Antipope Anacletus II. The council condemned heresies, enforced clerical celibacy by invalidating marriages of priests, and prohibited tournaments as too dangerous.",
            "We prohibit under anathema that murderous art of crossbowmen and archers, which is hateful to God.",
            "Second Lateran Council, Canon 29, 1139 AD",
            Arrays.asList(
                Lesson.quiz("10-1", "Medieval Canon Law",
                    "The Second Lateran Council controversially banned the use of a certain weapon against Christians. Which weapon?",
                    new String[]{"The longbow", "The crossbow", "Greek fire", "The trebuchet"},
                    1,
                    "Canon 29 banned crossbows as \"hateful to God\" when used against Christians. The church sought to limit casualties in warfare among Christian peoples, though the ban was widely ignored.")
            ));
    }

    private static Council c11() {
        return new Council(
            11, 11,
            "Third Lateran Council",
            "1179 AD",
            "Rome",
            "Medieval Church", 0xFF1A5C3A,
            "Catharism / Waldensianism",
            "Established 2/3 majority for papal elections; condemned Cathars",
            "Pope Alexander III", "Bishop of Rome",
            "Reformed papal elections by requiring a two-thirds supermajority of cardinals — a rule still in use today. It also condemned the Cathar and Waldensian heresies spreading through southern France.",
            "We ordain that if anyone is elected by less than two-thirds of the cardinals, he is not to be regarded as pope.",
            "Third Lateran Council, Canon 1, 1179 AD",
            Arrays.asList(
                Lesson.quiz("11-1", "Papal Elections",
                    "What supermajority did the Third Lateran Council establish for valid papal elections, still used today?",
                    new String[]{"Simple majority (50%+1)", "Three-quarters (75%)", "Two-thirds (66.6%)", "Unanimous agreement"},
                    2,
                    "The two-thirds supermajority requirement, established in 1179, remains in force for papal conclaves today. It prevents a simple majority faction from dominating elections.")
            ));
    }

    private static Council c12() {
        return new Council(
            12, 12,
            "Fourth Lateran Council",
            "1215 AD",
            "Rome",
            "Medieval Church", 0xFF1A5C3A,
            "Catharism / Albigensianism",
            "Transubstantiation defined; annual confession required; Jews must wear identifying dress",
            "Pope Innocent III", "Bishop of Rome",
            "One of the most consequential medieval councils, attended by over 1,200 churchmen. It formally defined transubstantiation, mandated annual confession for all Catholics, called for a Crusade, and issued controversial regulations regarding Jews and heretics.",
            "The body and blood of Jesus Christ is truly contained in the sacrament of the altar under the forms of bread and wine.",
            "Fourth Lateran Council, Canon 1, 1215 AD",
            Arrays.asList(
                Lesson.info("12-1", "Transubstantiation",
                    "The Fourth Lateran Council first used the term \"transubstantiation\" to describe the Eucharist: the substance of bread and wine truly becomes the body and blood of Christ, while the accidents (appearance, taste) remain. This became the cornerstone of Catholic Eucharistic theology."),
                Lesson.quiz("12-2", "The Easter Duty",
                    "The Fourth Lateran Council mandated that Catholics receive Communion and make Confession at minimum how often?",
                    new String[]{"Weekly", "Monthly", "At least once a year, at Easter", "Whenever convenient"},
                    2,
                    "The \"Easter Duty\" — annual confession and Communion at Easter — was established by Canon 21. This minimum standard shaped Catholic practice for centuries.")
            ));
    }

    private static Council c13() {
        return new Council(
            13, 13,
            "First Council of Lyon",
            "1245 AD",
            "Lyon, France",
            "Medieval Church", 0xFF1A5C3A,
            "Imperial overreach",
            "Deposed Emperor Frederick II; called for Crusade against Mongols",
            "Pope Innocent IV", "Bishop of Rome",
            "Held as Pope Innocent IV fled Italy to escape Holy Roman Emperor Frederick II. The council formally deposed Frederick II — the most dramatic papal act against a secular ruler — and called for a crusade against the Mongol invasion of Europe.",
            "We declare the aforesaid prince, who has rendered himself so unworthy... to be bound by his sins.",
            "First Council of Lyon, 1245 AD",
            Arrays.asList(
                Lesson.quiz("13-1", "Papal-Imperial Conflict",
                    "What extraordinary action did the First Council of Lyon take against Holy Roman Emperor Frederick II?",
                    new String[]{"Excommunicated him for the first time", "Crowned him emperor of Jerusalem", "Formally deposed him as emperor", "Absolved him of heresy charges"},
                    2,
                    "The council formally deposed Frederick II — the most extreme exercise of papal authority over temporal rulers. Frederick never accepted the deposition, and the conflict continued until his death in 1250.")
            ));
    }

    private static Council c14() {
        return new Council(
            14, 14,
            "Second Council of Lyon",
            "1274 AD",
            "Lyon, France",
            "Medieval Church", 0xFF1A5C3A,
            "East-West Schism",
            "Temporary reunion of Eastern and Western churches; defined Filioque",
            "St. Thomas Aquinas", "Dominican Theologian (died en route)",
            "Achieved a short-lived reunion between Rome and Constantinople, with the Byzantine Emperor Michael VIII accepting the Filioque clause. St. Thomas Aquinas died traveling to the council. The reunion collapsed within a decade due to Byzantine popular opposition.",
            "The Holy Spirit proceeds eternally from the Father and the Son, not as from two principles, but as from one principle.",
            "Second Council of Lyon, 1274 AD",
            Arrays.asList(
                Lesson.quiz("14-1", "The Filioque",
                    "The Filioque controversy is about the procession of which person of the Trinity?",
                    new String[]{"God the Father", "God the Son", "The Holy Spirit", "All three equally"},
                    2,
                    "The Filioque (\"and the Son\") was added to the Western creed: the Spirit proceeds \"from the Father AND the Son.\" The Orthodox rejected this as a unilateral Western addition changing the agreed creed.")
            ));
    }

    private static Council c15() {
        return new Council(
            15, 15,
            "Council of Vienne",
            "1311–1312 AD",
            "Vienne, France",
            "Late Medieval", 0xFF7A1C1C,
            "Knights Templar / Beguines",
            "Dissolved the Knights Templar; condemned Spiritual Franciscans",
            "Pope Clement V", "Bishop of Rome (Avignon Papacy)",
            "Held under pressure from King Philip IV of France, this council controversially dissolved the Knights Templar — one of the richest medieval institutions — amid accusations of heresy. It also condemned the Beguines and Spiritual Franciscans.",
            "Not by way of judicial condemnation but by way of apostolic provision, we suppress... the order of Knights Templar.",
            "Council of Vienne, 1312 AD",
            Arrays.asList(
                Lesson.quiz("15-1", "The Knights Templar",
                    "Who primarily pressured Pope Clement V to dissolve the Knights Templar at the Council of Vienne?",
                    new String[]{"The Holy Roman Emperor", "King Philip IV of France", "The Byzantine Emperor", "The Dominican Order"},
                    1,
                    "King Philip IV of France owed enormous debts to the Templars. He orchestrated mass arrests and torture-extracted confessions, forcing Pope Clement V (who resided in French Avignon) to dissolve the order.")
            ));
    }

    private static Council c16() {
        return new Council(
            16, 16,
            "Council of Constance",
            "1414–1418 AD",
            "Constance, Germany",
            "Late Medieval", 0xFF7A1C1C,
            "Great Western Schism / Hussitism",
            "Ended three-pope schism; condemned Jan Hus; elected Martin V",
            "Jan Hus", "Czech Reformer (martyred)",
            "The council that ended the catastrophic Western Schism — a period with three simultaneous claimants to the papacy. It also burned the Czech reformer Jan Hus at the stake despite a guarantee of safe conduct, an act that sparked the Hussite Wars.",
            "O holy simplicity!",
            "Jan Hus at the stake, 1415 AD (reportedly said of an old woman adding wood)",
            Arrays.asList(
                Lesson.quiz("16-1", "The Western Schism",
                    "At its worst, the Great Western Schism had how many simultaneous claimants to the papacy?",
                    new String[]{"Two", "Three", "Four", "Five"},
                    1,
                    "At its peak (1409–1415) there were three claimants: one in Rome, one in Avignon, and one elected at the Council of Pisa. The Council of Constance resolved this by deposing all three and electing Martin V.")
            ));
    }

    private static Council c17() {
        return new Council(
            17, 17,
            "Council of Basel / Ferrara / Florence",
            "1431–1449 AD",
            "Basel → Ferrara → Florence",
            "Late Medieval", 0xFF7A1C1C,
            "Conciliarism / East-West Schism",
            "Achieved temporary union with Eastern churches; defeated Conciliarism",
            "Pope Eugene IV", "Bishop of Rome",
            "A tumultuous council that moved locations three times. It achieved another temporary reunion with the Eastern Church (and briefly with Armenians, Copts, and Ethiopians) but collapsed as Constantinople fell to the Ottomans in 1453.",
            "We define that the holy Apostolic See and the Roman Pontiff hold the primacy over the whole world.",
            "Council of Florence, 1439 AD",
            Arrays.asList(
                Lesson.quiz("17-1", "The Fall of Constantinople",
                    "What event in 1453 effectively ended the reunion achieved at the Council of Florence?",
                    new String[]{"Death of Pope Eugene IV", "Ottoman Turks conquered Constantinople", "The Greek emperor was assassinated", "A new ecumenical council rejected the union"},
                    1,
                    "When Mehmed II conquered Constantinople on May 29, 1453, the Byzantine Empire ended. The union had already been rejected by most Eastern Christians, and the Ottoman conquest made the question moot.")
            ));
    }

    private static Council c18() {
        return new Council(
            18, 18,
            "Fifth Lateran Council",
            "1512–1517 AD",
            "Rome",
            "Reformation Era", 0xFF1A3A5C,
            "Conciliarism / Church abuses",
            "Condemned Conciliarism; minor reform decrees",
            "Pope Julius II", "Bishop of Rome",
            "Called to counter a schismatic council organized by King Louis XII of France, this council condemned Conciliarism but failed to enact the sweeping reforms needed. It closed just months before Luther posted his 95 Theses, having missed the last chance to prevent the Reformation.",
            "All power has been given to us by the Supreme Pontiff.",
            "Fifth Lateran Council, 1516 AD",
            Arrays.asList(
                Lesson.quiz("18-1", "A Missed Opportunity",
                    "The Fifth Lateran Council closed in March 1517. What world-changing event happened just seven months later?",
                    new String[]{"The Sack of Rome by Charles V", "Columbus's fourth voyage", "Luther posted his 95 Theses", "Henry VIII's break with Rome"},
                    2,
                    "Martin Luther posted his 95 Theses on October 31, 1517 — just months after the council closed without serious reform. Many historians see the council's failure as a missed opportunity to prevent the Protestant Reformation.")
            ));
    }

    private static Council c19() {
        return new Council(
            19, 19,
            "Council of Trent",
            "1545–1563 AD",
            "Trent, Italy",
            "Reformation Era", 0xFF1A3A5C,
            "Protestantism",
            "Defined Catholic doctrine on Scripture, Justification, Sacraments vs. Protestant claims",
            "Cardinal Reginald Pole", "Papal Legate",
            "The great Counter-Reformation council, spanning 18 years across three periods. It systematically defined Catholic doctrine in response to Protestant challenges: Scripture and Tradition as dual sources, justification by faith and works, seven sacraments, transubstantiation, Purgatory, and veneration of saints.",
            "If anyone says that man can be justified before God by his own works... let him be anathema.",
            "Council of Trent, Session VI, 1547 AD",
            Arrays.asList(
                Lesson.info("19-1", "Justification by Faith",
                    "The central Reformation debate was about justification: how is a sinner made right with God? Luther taught \"sola fide\" — faith alone. Trent affirmed that justification involves both faith and cooperation with grace (works of charity), rejecting both pure works-righteousness and pure antinomianism."),
                Lesson.quiz("19-2", "Scripture and Tradition",
                    "Trent's response to the Protestant \"sola scriptura\" was to affirm what two equal sources of revelation?",
                    new String[]{"Scripture and the Pope", "Scripture and Sacred Tradition", "The Bible and Church Councils", "The Old and New Testaments"},
                    1,
                    "Trent affirmed Scripture and Sacred Tradition as two co-equal sources of revelation, received with equal reverence. This directly opposed the Protestant principle of Scripture alone (sola scriptura).")
            ));
    }

    private static Council c20() {
        return new Council(
            20, 20,
            "First Vatican Council",
            "1869–1870 AD",
            "Rome",
            "Modern Church", 0xFF2C3E50,
            "Rationalism / Gallicanism",
            "Papal Infallibility defined; Papal Primacy affirmed",
            "Pope Pius IX", "Bishop of Rome",
            "The first council in 300 years, interrupted by the seizure of Rome by Italian nationalist forces. It defined papal infallibility — the pope speaks infallibly when defining doctrine on faith and morals ex cathedra — and papal primacy of jurisdiction over the whole church.",
            "The Roman Pontiff, when he speaks ex cathedra... is possessed of that infallibility with which the divine Redeemer willed that his Church should be endowed.",
            "Pastor Aeternus, 1870 AD",
            Arrays.asList(
                Lesson.info("20-1", "Ex Cathedra",
                    "Papal infallibility applies only when the pope speaks \"ex cathedra\" (from the chair): formally defining a doctrine on faith or morals, with the intent to bind the whole Church. This is extremely rare — only once since Vatican I (Assumption of Mary, 1950) — despite common misunderstanding that everything the pope says is infallible."),
                Lesson.quiz("20-2", "Why was Vatican I interrupted?",
                    "What political event caused Vatican I to be suspended before completing its agenda?",
                    new String[]{"Death of Pope Pius IX", "Franco-Prussian War and Italian forces seized Rome", "Outbreak of cholera in Rome", "Cardinal revolt against infallibility definition"},
                    1,
                    "When French troops protecting the papal states withdrew for the Franco-Prussian War (1870), Italian nationalist forces seized Rome. Pius IX suspended the council, and it was never formally closed.")
            ));
    }

    private static Council c21() {
        return new Council(
            21, 21,
            "Second Vatican Council",
            "1962–1965 AD",
            "Rome",
            "Modern Church", 0xFF2C3E50,
            "Modernism / Atheism",
            "Liturgical reform; religious liberty; ecumenism; church in the modern world",
            "Pope John XXIII", "Bishop of Rome (convened)",
            "The most recent and most attended council in history (~2,500 bishops). Convened by Pope John XXIII as an \"aggiornamento\" (updating), it produced sixteen documents reforming the liturgy, affirming religious liberty, promoting ecumenism, and redefining the Church's relationship with the modern world.",
            "The Church... reads the signs of the times and interprets them in the light of the Gospel.",
            "Gaudium et Spes, Vatican II, 1965 AD",
            Arrays.asList(
                Lesson.info("21-1", "Aggiornamento",
                    "Pope John XXIII's word \"aggiornamento\" (bringing up to date) captured Vatican II's spirit. The council sought to express timeless truths in ways contemporary people could understand — not changing doctrine, but renewing its presentation. The Mass was reformed, vernacular languages permitted, and the church's posture toward the world shifted."),
                Lesson.quiz("21-2", "Vatican II Documents",
                    "What is the name of Vatican II's constitution on the Church in the Modern World?",
                    new String[]{"Lumen Gentium", "Dei Verbum", "Gaudium et Spes", "Sacrosanctum Concilium"},
                    2,
                    "\"Gaudium et Spes\" (Joy and Hope) addresses the Church's relationship with the modern world. Lumen Gentium is on the Church itself; Dei Verbum on Divine Revelation; Sacrosanctum Concilium on the Liturgy.")
            ));
    }
}
