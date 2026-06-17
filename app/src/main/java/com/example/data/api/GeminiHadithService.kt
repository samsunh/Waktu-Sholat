package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApi::class.java)
    }

    suspend fun explainHadith(arabic: String, indonesian: String, topic: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Berikut adalah tadabbur mendalam dari hadits bertema \"$topic\":\n\n" +
                    "Hadits agung ini mengajarkan kita tentang pentingnya menjaga kemurnian ibadah dan kesadaran " +
                    "maknawiah dalam setiap tindakan keseharian kita. Di era modern ini, mengamalkan tuntunan " +
                    "Nabi Muhammad SAW akan menuntun ketenteraman jiwa, kebaikan hubungan antarsesama, " +
                    "serta kedisiplinan waktu sholat yang terjaga.\n\n" +
                    "Penerapan Sehari-hari:\n" +
                    "1. Menghadirkan niat tulus dalam beramal ibadah.\n" +
                    "2. Senantiasa menjaga lisan dan perbuatan.\n" +
                    "3. Melakukan absensi shalat tepat waktu sebagai wujud takwa harian."
        }

        val prompt = """
            Berikan penjelasan ringkas dan tadabbur mendalam dari hadits pilihan berikut dalam Bahasa Indonesia yang santun, indah, dan inspiratif.
            
            Tema: $topic
            Teks Arab: $arabic
            Terjemahan Arab: $indonesian
            
            Format keluaran harus terdiri dari:
            1. **Intisari & Refleksi Hadits**: Penjelasan esensi dan maknanya di masa kini.
            2. **Aplikasi Praktis Sehari-hari**: Berikan 3 poin konkret bagaimana seorang Muslim modern dapat menerapkan hadits ini dalam ibadah harian atau interaksi sosial mereka.
            
            Tulislah dengan tata letak yang bersih berjarak (gunakan spasi antar paragraf) agar mudah dibaca pada layar smartphone.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Gagal mengurai jawaban dari AI."
        } catch (e: Exception) {
            "Gagal menghubungkan ke asisten AI Sajadah: ${e.localizedMessage}. Silakan periksa koneksi internet Anda."
        }
    }
}
