package com.noor.prayertv.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noor.prayertv.data.PrayerRepository
import com.noor.prayertv.data.models.CalculationMethods
import com.noor.prayertv.data.models.Cities
import com.noor.prayertv.data.models.City
import com.noor.prayertv.data.models.PrayerInfo
import com.noor.prayertv.data.models.TimingData
import com.noor.prayertv.data.models.toPrayerList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Application.dataStore by preferencesDataStore(name = "noor_prefs")

data class PrayerUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val timingData: TimingData? = null,
    val prayers: List<PrayerInfo> = emptyList(), // 6 مواقيت (5 فروض + الشروق)
    val nextPrayer: PrayerInfo? = null,
    val nextPrayerCountdown: String = "--:--:--",
    val hijriDate: String = "",
    val hijriDateAr: String = "",
    val gregorianDate: String = "",
    val city: City = Cities.default,
    val methodId: Int = 4,
    val school: Int = 0, // 0 Shafi, 1 Hanafi
    val currentTime: String = "",
    val isRefreshing: Boolean = false
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PrayerRepository()
    private val app = application

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState

    private val _timeTicker = MutableStateFlow(System.currentTimeMillis())

    companion object {
        private val KEY_CITY_INDEX = intPreferencesKey("city_index")
        private val KEY_METHOD = intPreferencesKey("method_id")
        private val KEY_SCHOOL = intPreferencesKey("school")
        // Custom city persistence keys
        private val KEY_CUSTOM_NAME_AR = stringPreferencesKey("custom_city_nameAr")
        private val KEY_CUSTOM_NAME_EN = stringPreferencesKey("custom_city_nameEn")
        private val KEY_CUSTOM_COUNTRY_AR = stringPreferencesKey("custom_countryAr")
        private val KEY_CUSTOM_COUNTRY_EN = stringPreferencesKey("custom_countryEn")
        private val KEY_CUSTOM_LAT = doublePreferencesKey("custom_lat")
        private val KEY_CUSTOM_LON = doublePreferencesKey("custom_lon")
        private val KEY_CUSTOM_TZ = stringPreferencesKey("custom_tz")
        private val KEY_IS_CUSTOM = booleanPreferencesKey("is_custom")
    }

    init {
        viewModelScope.launch {
            val prefs = app.dataStore.data.first()
            val cityIdx = prefs[KEY_CITY_INDEX] ?: 0
            val method = prefs[KEY_METHOD] ?: 4
            val school = prefs[KEY_SCHOOL] ?: 0
            val isCustom = prefs[KEY_IS_CUSTOM] ?: false
            val city = if (isCustom) {
                try {
                    City(
                        nameAr = prefs[KEY_CUSTOM_NAME_AR] ?: Cities.default.nameAr,
                        nameEn = prefs[KEY_CUSTOM_NAME_EN] ?: Cities.default.nameEn,
                        countryAr = prefs[KEY_CUSTOM_COUNTRY_AR] ?: Cities.default.countryAr,
                        countryEn = prefs[KEY_CUSTOM_COUNTRY_EN] ?: Cities.default.countryEn,
                        latitude = prefs[KEY_CUSTOM_LAT] ?: Cities.default.latitude,
                        longitude = prefs[KEY_CUSTOM_LON] ?: Cities.default.longitude,
                        timeZone = prefs[KEY_CUSTOM_TZ] ?: Cities.default.timeZone
                    )
                } catch (e: Exception) {
                    Cities.all.getOrElse(cityIdx) { Cities.default }
                }
            } else {
                Cities.all.getOrElse(cityIdx) { Cities.default }
            }
            _uiState.value = _uiState.value.copy(city = city, methodId = method, school = school)
            refresh()
            startTicker()
        }
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _timeTicker.value = System.currentTimeMillis()
                updateCountdown()
                updateCurrentTime()
            }
        }
    }

    private fun startTicker() {
        updateCurrentTime()
    }

    private fun updateCurrentTime() {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        _uiState.value = _uiState.value.copy(currentTime = sdf.format(Date()))
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, isRefreshing = true)
            val city = _uiState.value.city
            val method = _uiState.value.methodId
            val school = _uiState.value.school

            // 100% للأردن: جلب الجدول الرسمي للأوقاف أولاً
            val official = repo.getOfficialJordanIfNeeded(city)

            val result = repo.getTimings(city.latitude, city.longitude, method, school, city.timeZone)
            result.onSuccess { resp ->
                val data = resp.data
                val hijri = data.date.hijri
                val hijriStr = "${hijri.day} ${hijri.month.en} ${hijri.year} AH"
                val hijriAr = "${hijri.day} ${hijri.month.ar} ${hijri.year} هـ"
                val greg = data.date.gregorian
                val gregStr = "${greg.weekday.en}, ${greg.day} ${greg.month.en} ${greg.year}"

                val nowMinutes = minutesNow()
                // استخدم الرسمي للأردن إن وجد، وإلا استخدم Aladhan (method 23 للأردن = 100% للفروض، 5د فرق شروق فقط)
                val timings = if (official != null) {
                    data.timings.copy(
                        fajr = official.fajr,
                        sunrise = official.sunrise,
                        dhuhr = official.dhuhr,
                        asr = official.asr,
                        maghrib = official.maghrib,
                        isha = official.isha
                    )
                } else {
                    data.timings
                }

                val ordered = listOf(
                    "Fajr" to timings.fajr,
                    "Sunrise" to timings.sunrise,
                    "Dhuhr" to timings.dhuhr,
                    "Asr" to timings.asr,
                    "Maghrib" to timings.maghrib,
                    "Isha" to timings.isha
                )
                var nextName: String? = null
                for ((name, time) in ordered) {
                    if (toMinutes(time) > nowMinutes) { nextName = name; break }
                }
                if (nextName == null) nextName = "Fajr"

                val allPrayers = timings.toPrayerList(nextName, nowMinutes)
                val prayers = allPrayers

                // استبدال timings داخل data للـ UI calendar إن لزم
                val mergedData = data.copy(timings = timings)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    timingData = mergedData,
                    prayers = prayers,
                    nextPrayer = prayers.find { it.isNext },
                    hijriDate = hijriStr,
                    hijriDateAr = hijriAr,
                    gregorianDate = gregStr,
                    error = null
                )
                updateCountdown()
            }.onFailure { e ->
                // حتى لو فشل Aladhan، حاول عرض الرسمي وحده (للأردن)
                if (official != null) {
                    val nowMinutes = minutesNow()
                    val timings = com.noor.prayertv.data.models.Timings(
                        fajr = official.fajr,
                        sunrise = official.sunrise,
                        dhuhr = official.dhuhr,
                        asr = official.asr,
                        sunset = official.maghrib,
                        maghrib = official.maghrib,
                        isha = official.isha,
                        imsak = official.fajr,
                        midnight = "00:00",
                        firstthird = "00:00",
                        lastthird = "00:00"
                    )
                    val allPrayers = timings.toPrayerList(null, nowMinutes)
                    // حدد القادمة يدوياً
                    val orderedF = listOf("Fajr" to timings.fajr, "Sunrise" to timings.sunrise, "Dhuhr" to timings.dhuhr, "Asr" to timings.asr, "Maghrib" to timings.maghrib, "Isha" to timings.isha)
                    var nextN: String? = null
                    for ((n, t) in orderedF) if (toMinutes(t) > nowMinutes) { nextN = n; break }
                    if (nextN == null) nextN = "Fajr"
                    val prayers2 = timings.toPrayerList(nextN, nowMinutes)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        prayers = prayers2,
                        nextPrayer = prayers2.find { it.isNext },
                        hijriDate = "",
                        gregorianDate = "",
                        error = null
                    )
                    updateCountdown()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "خطأ في الاتصال - تأكد من الإنترنت"
                    )
                }
            }
        }
    }

    private fun updateCountdown() {
        val next = _uiState.value.nextPrayer ?: return
        val now = Calendar.getInstance()
        val nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val nowSec = now.get(Calendar.SECOND)
        val targetMin = toMinutes(next.time)
        var diffSec = targetMin * 60 - (nowMin * 60 + nowSec)
        if (diffSec < 0) diffSec += 24 * 3600
        val h = diffSec / 3600
        val m = (diffSec % 3600) / 60
        val s = diffSec % 60
        val countdown = String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s)
        if (_uiState.value.nextPrayerCountdown != countdown) {
            _uiState.value = _uiState.value.copy(nextPrayerCountdown = countdown)
        }
    }

    private fun toMinutes(t: String): Int {
        val p = t.split(":")
        return p[0].toInt() * 60 + p[1].toInt()
    }

    private fun minutesNow(): Int {
        val c = Calendar.getInstance()
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
    }

    private fun methodForCity(city: City): Int = when (city.countryEn.lowercase()) {
        "egypt" -> 5 // الهيئة المصرية
        "turkey" -> 13 // Diyanet
        "algeria" -> 19
        "tunisia" -> 3 // MWL
        "morocco" -> 3
        "sudan" -> 5
        "france" -> 12
        "germany" -> 3
        "uk", "united kingdom" -> 3
        "usa", "united states" -> 2 // ISNA
        "indonesia" -> 20
        "malaysia" -> 3
        "emirates", "uae" -> 8 // Gulf
        "qatar" -> 8
        "kuwait" -> 8
        "bahrain" -> 8
        "oman" -> 8
        else -> 4 // أم القرى للسعودية والأردن وفلسطين ولبنان وسوريا والعراق واليمن وليبيا وغالب العربية
    }

    fun selectCity(index: Int) {
        val city = Cities.all.getOrElse(index) { Cities.default }
        val autoMethod = methodForCity(city)
        viewModelScope.launch {
            app.dataStore.edit {
                it[KEY_CITY_INDEX] = index
                it[KEY_IS_CUSTOM] = false
                it[KEY_METHOD] = autoMethod
            }
            _uiState.value = _uiState.value.copy(city = city, methodId = autoMethod)
            refresh()
        }
    }

    fun selectCustomCity(city: City) {
        val autoMethod = methodForCity(city)
        viewModelScope.launch {
            app.dataStore.edit {
                it[KEY_CUSTOM_NAME_AR] = city.nameAr
                it[KEY_CUSTOM_NAME_EN] = city.nameEn
                it[KEY_CUSTOM_COUNTRY_AR] = city.countryAr
                it[KEY_CUSTOM_COUNTRY_EN] = city.countryEn
                it[KEY_CUSTOM_LAT] = city.latitude
                it[KEY_CUSTOM_LON] = city.longitude
                it[KEY_CUSTOM_TZ] = city.timeZone
                it[KEY_IS_CUSTOM] = true
                it[KEY_METHOD] = autoMethod
            }
            _uiState.value = _uiState.value.copy(city = city, methodId = autoMethod)
            refresh()
        }
    }

    fun selectMethod(id: Int) {
        viewModelScope.launch {
            app.dataStore.edit { it[KEY_METHOD] = id }
            _uiState.value = _uiState.value.copy(methodId = id)
            refresh()
        }
    }

    fun selectSchool(school: Int) {
        viewModelScope.launch {
            app.dataStore.edit { it[KEY_SCHOOL] = school }
            _uiState.value = _uiState.value.copy(school = school)
            refresh()
        }
    }

    fun getMethodName(): String {
        return CalculationMethods.all.find { it.id == _uiState.value.methodId }?.nameAr ?: "أم القرى"
    }
}
