package com.example.model

data class Hadith(
    val arabic: String,
    val indonesian: String,
    val narrator: String,
    val book: String,
    val topic: String
)

val CURATED_HADITHS = listOf(
    Hadith(
        arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
        indonesian = "Sesungguhnya setiap amalan itu bergantung pada niatnya, dan setiap orang akan mendapatkan apa yang ia niatkan.",
        narrator = "Hadits riwayat Umar bin Khattab r.a.",
        book = "Sahih Bukhari & Muslim",
        topic = "Niat dan Keikhlasan"
    ),
    Hadith(
        arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
        indonesian = "Barangsiapa yang beriman kepada Allah dan Hari Akhir, maka hendaklah ia berkata yang baik atau diam.",
        narrator = "Hadits riwayat Abu Hurairah r.a.",
        book = "Sahih Bukhari & Muslim",
        topic = "Adab Berbicara"
    ),
    Hadith(
        arabic = "احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ",
        indonesian = "Jagalah Allah niscaya Dia akan menjagamu. Jagalah Allah niscaya kamu akan mendapati-Nya di hadapanmu.",
        narrator = "Hadits riwayat Ibnu Abbas r.a.",
        book = "Arbain Nawawi (Hadits 19)",
        topic = "Penjagaan Allah"
    ),
    Hadith(
        arabic = "لاَ يَدْخُلُ الْجَنَّةَ مَنْ كَانَ فِي قَلْبِهِ مِثْقَالُ ذَرَّةٍ مِنْ كِبْرٍ",
        indonesian = "Tidak akan masuk surga orang yang di dalam hatinya terdapat kesombongan sebesar biji sawi.",
        narrator = "Hadits riwayat Abdullah bin Mas'ud r.a.",
        book = "Sahih Muslim",
        topic = "Sifat Sombong"
    ),
    Hadith(
        arabic = "التَّقْوَى هَاهُنَا... وَيُشِيرُ إِلَى صَدْرِهِ ثَلاَثَ مَرَّاتٍ",
        indonesian = "Takwa itu ada di sini... seraya beliau menunjuk ke arah dadanya sebanyak tiga kali.",
        narrator = "Hadits riwayat Abu Hurairah r.a.",
        book = "Sahih Muslim",
        topic = "Hakikat Takwa"
    ),
    Hadith(
        arabic = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
        indonesian = "Seorang Muslim sejati adalah orang yang Muslim lainnya selamat dari gangguan lisan dan tangannya.",
        narrator = "Hadits riwayat Abdullah bin Umar r.a.",
        book = "Sahih Bukhari",
        topic = "Persaudaraan Islam"
    ),
    Hadith(
        arabic = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
        indonesian = "Barangsiapa menempuh suatu jalan untuk mencari ilmu, maka Allah akan memudahkan baginya jalan menuju surga.",
        narrator = "Hadits riwayat Abu Hurairah r.a.",
        book = "Sahih Muslim",
        topic = "Keutamaan Menuntut Ilmu"
    )
)
