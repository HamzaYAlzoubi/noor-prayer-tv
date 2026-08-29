package com.noor.prayertv.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

private data class JordanDay(
    @SerializedName("fajr") val fajr: String,
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("dhuhr") val dhuhr: String,
    @SerializedName("asr") val asr: String,
    @SerializedName("maghrib") val maghrib: String,
    @SerializedName("isha") val isha: String,
    @SerializedName("date") val date: String // MM-DD
)

object JordanOfficialDataSource {
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }
    private val gson = Gson()

    // خرائط المدينة العربية -> ملف JSON الرسمي
    private fun fileForCity(cityEn: String): String = when (cityEn.lowercase()) {
        "kharja" -> "irbid" // خرجا تابعة لمحافظة إربد رسمياً
        "irbid" -> "irbid"
        "amman" -> "amman"
        "zarqa" -> "amman" // لا يوجد ملف زرقة منفصل في الجدول الرسمي، الأقرب عمان
        "aqaba" -> "aqaba"
        "irhab" -> "irhab"
        "almafraq", "alMafraq", "mafraq" -> "alMafraq"
        "ajloun" -> "ajloun"
        "jerash" -> "jerash"
        "karak", "alkarak" -> "alkarak"
        "tafila" -> "tafila"
        "maan" -> "maan"
        else -> "irbid" // افتراضي إربد للأردن غير المعروف
    }

    // جلب يوم محدد من الجدول الرسمي، يعيد Timings الرسمية أو null
    suspend fun getOfficialTimings(cityEn: String, mmdd: String): JordanTimings? = withContext(Dispatchers.IO) {
        val file = fileForCity(cityEn)
        val urls = listOf(
            "https://raw.githubusercontent.com/Azzubairx/prayer-times-json/main/Jordan/$file.json",
            "https://raw.githubusercontent.com/Azzubairx/prayer-times-json/main/Jordan/amman.json" // fallback
        )
        for (url in urls) {
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "NoorPrayerTV/1.0")
                    .build()
                val resp = client.newCall(req).execute()
                if (!resp.isSuccessful) continue
                val body = resp.body?.string() ?: continue
                if (body.isBlank() || body == "[]") continue
                val type = object : TypeToken<List<JordanDay>>() {}.type
                val list: List<JordanDay> = gson.fromJson(body, type) ?: continue
                val day = list.find { it.date == mmdd } ?: continue
                return@withContext JordanTimings(
                    fajr = day.fajr,
                    sunrise = day.sunrise,
                    dhuhr = day.dhuhr,
                    asr = day.asr,
                    maghrib = day.maghrib,
                    isha = day.isha
                )
            } catch (e: Exception) {
                continue
            }
        }
        null
    }
}

data class JordanTimings(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)
