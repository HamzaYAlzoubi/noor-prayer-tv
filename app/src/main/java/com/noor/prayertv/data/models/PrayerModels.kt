package com.noor.prayertv.data.models

import com.google.gson.annotations.SerializedName

data class TimingsResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: TimingData
)

data class TimingData(
    @SerializedName("timings") val timings: Timings,
    @SerializedName("date") val date: DateInfo,
    @SerializedName("meta") val meta: Meta
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Sunset") val sunset: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
    @SerializedName("Imsak") val imsak: String,
    @SerializedName("Midnight") val midnight: String,
    @SerializedName("Firstthird") val firstthird: String,
    @SerializedName("Lastthird") val lastthird: String
)

data class DateInfo(
    @SerializedName("readable") val readable: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("hijri") val hijri: HijriDate,
    @SerializedName("gregorian") val gregorian: GregorianDate
)

data class HijriDate(
    @SerializedName("date") val date: String,
    @SerializedName("format") val format: String,
    @SerializedName("day") val day: String,
    @SerializedName("weekday") val weekday: Weekday,
    @SerializedName("month") val month: HijriMonth,
    @SerializedName("year") val year: String,
    @SerializedName("designation") val designation: Designation
)

data class HijriMonth(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String
)

data class GregorianDate(
    @SerializedName("date") val date: String,
    @SerializedName("format") val format: String,
    @SerializedName("day") val day: String,
    @SerializedName("weekday") val weekday: Weekday,
    @SerializedName("month") val month: GregorianMonth,
    @SerializedName("year") val year: String,
    @SerializedName("designation") val designation: Designation
)

data class Weekday(
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String? = null
)

data class GregorianMonth(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String
)

data class Designation(
    @SerializedName("abbreviated") val abbreviated: String,
    @SerializedName("expanded") val expanded: String
)

data class Meta(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("method") val method: MethodInfo,
    @SerializedName("school") val school: String,
    @SerializedName("latitudeAdjustmentMethod") val latitudeAdjustmentMethod: String,
    @SerializedName("midnightMode") val midnightMode: String,
    @SerializedName("offset") val offset: Map<String, Int>
)

data class MethodInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("params") val params: Map<String, Any>?
)

// Calendar response (monthly)
data class CalendarResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<TimingData>
)

// Qibla
data class QiblaResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: QiblaData
)

data class QiblaData(
    @SerializedName("direction") val direction: Double
)

// UI models
data class PrayerInfo(
    val nameEn: String,
    val nameAr: String,
    val time: String, // "04:45"
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

enum class PrayerType(val en: String, val ar: String) {
    FAJR("Fajr", "الفجر"),
    SUNRISE("Sunrise", "الشروق"),
    DHUHR("Dhuhr", "الظهر"),
    ASR("Asr", "العصر"),
    MAGHRIB("Maghrib", "المغرب"),
    ISHA("Isha", "العشاء")
}

fun Timings.toPrayerList(nextPrayerName: String? = null, nowMinutes: Int = -1): List<PrayerInfo> {
    fun toMinutes(t: String): Int {
        val parts = t.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }
    val list = listOf(
        PrayerType.FAJR to fajr,
        PrayerType.SUNRISE to sunrise,
        PrayerType.DHUHR to dhuhr,
        PrayerType.ASR to asr,
        PrayerType.MAGHRIB to maghrib,
        PrayerType.ISHA to isha
    )
    return list.map { (type, time) ->
        val isNext = type.en == nextPrayerName
        val isPassed = if (nowMinutes >= 0) toMinutes(time) < nowMinutes else false
        PrayerInfo(type.en, type.ar, time, isNext, isPassed && !isNext)
    }
}
