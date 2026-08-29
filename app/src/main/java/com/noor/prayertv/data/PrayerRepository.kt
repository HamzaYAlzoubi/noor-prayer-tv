package com.noor.prayertv.data

import com.noor.prayertv.data.models.CalendarResponse
import com.noor.prayertv.data.models.QiblaResponse
import com.noor.prayertv.data.models.TimingsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PrayerRepository {

    private val api: AladhanApi by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AladhanApi::class.java)
    }

    private fun todayDdMmYyyy(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date())
    }

    suspend fun getTimings(
        latitude: Double,
        longitude: Double,
        method: Int,
        school: Int,
        timeZone: String? = null
    ): Result<TimingsResponse> = try {
        val res = api.getTimings(todayDdMmYyyy(), latitude, longitude, method, school, timeZone)
        if (res.code == 200) Result.success(res) else Result.failure(Exception(res.status))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getTimingsByCity(city: String, country: String, method: Int, school: Int): Result<TimingsResponse> = try {
        val res = api.getTimingsByCity(todayDdMmYyyy(), city, country, method, school)
        if (res.code == 200) Result.success(res) else Result.failure(Exception(res.status))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCalendar(year: Int, month: Int, lat: Double, lon: Double, method: Int, school: Int): Result<CalendarResponse> = try {
        val res = api.getCalendar(year, month, lat, lon, method, school)
        if (res.code == 200) Result.success(res) else Result.failure(Exception(res.status))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getQibla(lat: Double, lon: Double): Result<QiblaResponse> = try {
        val res = api.getQibla(lat, lon)
        if (res.code == 200) Result.success(res) else Result.failure(Exception(res.status))
    } catch (e: Exception) {
        Result.failure(e)
    }
    // Qibla محذوف من UI التلفاز - لكن نبقي الدالة للتوافق
}
