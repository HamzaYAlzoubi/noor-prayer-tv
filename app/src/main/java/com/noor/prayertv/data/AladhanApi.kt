package com.noor.prayertv.data

import com.noor.prayertv.data.models.CalendarResponse
import com.noor.prayertv.data.models.QiblaResponse
import com.noor.prayertv.data.models.TimingsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AladhanApi {

    // Timings by coordinates - primary, most accurate
    @GET("v1/timings/{date}")
    suspend fun getTimings(
        @Path("date") date: String, // dd-MM-yyyy
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4,
        @Query("school") school: Int = 0, // 0 Shafi, 1 Hanafi
        @Query("timezonestring") timeZone: String? = null,
        @Query("iso8601") iso8601: Boolean = false
    ): TimingsResponse

    @GET("v1/timingsByCity/{date}")
    suspend fun getTimingsByCity(
        @Path("date") date: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 4,
        @Query("school") school: Int = 0
    ): TimingsResponse

    // Monthly calendar - 1 call = 30 days (perfect for TV caching)
    @GET("v1/calendar/{year}/{month}")
    suspend fun getCalendar(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4,
        @Query("school") school: Int = 0
    ): CalendarResponse

    @GET("v1/qibla/{latitude}/{longitude}")
    suspend fun getQibla(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): QiblaResponse
}
