package com.noor.prayertv.data.models

data class City(
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val countryEn: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String
)

object Cities {
    val all = listOf(
        // الأردن - أولاً وافتراضي: خرجا لواء بني كنانة إربد
        City("خرجا", "Kharja", "الأردن - بني كنانة", "Jordan - Bani Kinanah", 32.65988, 35.88782, "Asia/Amman"),
        City("إربد", "Irbid", "الأردن", "Jordan", 32.55556, 35.85, "Asia/Amman"),
        City("عمان", "Amman", "الأردن", "Jordan", 31.9454, 35.9284, "Asia/Amman"),
        City("الزرقاء", "Zarqa", "الأردن", "Jordan", 32.0728, 36.0872, "Asia/Amman"),
        City("العقبة", "Aqaba", "الأردن", "Jordan", 29.5267, 35.0078, "Asia/Amman"),
        // السعودية
        City("مكة المكرمة", "Makkah", "السعودية", "Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        City("المدينة المنورة", "Madinah", "السعودية", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh"),
        City("الرياض", "Riyadh", "السعودية", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh"),
        City("جدة", "Jeddah", "السعودية", "Saudi Arabia", 21.5433, 39.1728, "Asia/Riyadh"),
        City("الدمام", "Dammam", "السعودية", "Saudi Arabia", 26.4207, 50.0888, "Asia/Riyadh"),
        // مصر
        City("القاهرة", "Cairo", "مصر", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        City("الإسكندرية", "Alexandria", "مصر", "Egypt", 31.2001, 29.9187, "Africa/Cairo"),
        // الإمارات
        City("دبي", "Dubai", "الإمارات", "UAE", 25.2048, 55.2708, "Asia/Dubai"),
        City("أبوظبي", "Abu Dhabi", "الإمارات", "UAE", 24.4539, 54.3773, "Asia/Dubai"),
        City("الشارقة", "Sharjah", "الإمارات", "UAE", 25.3573, 55.4033, "Asia/Dubai"),
        // قطر - الكويت - البحرين - عمان
        City("الدوحة", "Doha", "قطر", "Qatar", 25.2854, 51.5310, "Asia/Qatar"),
        City("الكويت", "Kuwait City", "الكويت", "Kuwait", 29.3759, 47.9774, "Asia/Kuwait"),
        City("المنامة", "Manama", "البحرين", "Bahrain", 26.2285, 50.5860, "Asia/Bahrain"),
        City("مسقط", "Muscat", "عمان", "Oman", 23.5880, 58.3829, "Asia/Muscat"),
        // اليمن
        City("صنعاء", "Sanaa", "اليمن", "Yemen", 15.3694, 44.1910, "Asia/Aden"),
        City("عدن", "Aden", "اليمن", "Yemen", 12.7794, 45.0360, "Asia/Aden"),
        // العراق
        City("بغداد", "Baghdad", "العراق", "Iraq", 33.3152, 44.3661, "Asia/Baghdad"),
        City("البصرة", "Basra", "العراق", "Iraq", 30.5085, 47.7835, "Asia/Baghdad"),
        City("أربيل", "Erbil", "العراق", "Iraq", 36.1911, 44.0091, "Asia/Baghdad"),
        // سوريا
        City("دمشق", "Damascus", "سوريا", "Syria", 33.5138, 36.2765, "Asia/Damascus"),
        City("حلب", "Aleppo", "سوريا", "Syria", 36.2021, 37.1343, "Asia/Damascus"),
        // لبنان
        City("بيروت", "Beirut", "لبنان", "Lebanon", 33.8886, 35.4955, "Asia/Beirut"),
        // فلسطين
        City("القدس", "Jerusalem", "فلسطين", "Palestine", 31.7683, 35.2137, "Asia/Hebron"),
        City("غزة", "Gaza", "فلسطين", "Palestine", 31.5017, 34.4668, "Asia/Hebron"),
        City("رام الله", "Ramallah", "فلسطين", "Palestine", 31.9038, 35.2034, "Asia/Hebron"),
        // السودان
        City("الخرطوم", "Khartoum", "السودان", "Sudan", 15.5007, 32.5599, "Africa/Khartoum"),
        // ليبيا
        City("طرابلس", "Tripoli", "ليبيا", "Libya", 32.8872, 13.1913, "Africa/Tripoli"),
        City("بنغازي", "Benghazi", "ليبيا", "Libya", 32.1194, 20.0681, "Africa/Tripoli"),
        // تونس
        City("تونس", "Tunis", "تونس", "Tunisia", 36.8065, 10.1815, "Africa/Tunis"),
        // الجزائر
        City("الجزائر", "Algiers", "الجزائر", "Algeria", 36.7538, 3.0588, "Africa/Algiers"),
        // المغرب
        City("الدار البيضاء", "Casablanca", "المغرب", "Morocco", 33.5731, -7.5898, "Africa/Casablanca"),
        City("الرباط", "Rabat", "المغرب", "Morocco", 34.0209, -6.8417, "Africa/Casablanca"),
        // موريتانيا
        City("نواكشوط", "Nouakchott", "موريتانيا", "Mauritania", 18.0735, -15.9582, "Africa/Nouakchott"),
        // الصومال
        City("مقديشو", "Mogadishu", "الصومال", "Somalia", 2.0469, 45.3182, "Africa/Mogadishu"),
        // جيبوتي
        City("جيبوتي", "Djibouti", "جيبوتي", "Djibouti", 11.5721, 43.1456, "Africa/Djibouti"),
        // جزر القمر
        City("موروني", "Moroni", "جزر القمر", "Comoros", -11.7022, 43.2551, "Indian/Comoro")
    )
    val default = all[0] // خرجا - بني كنانة - إربد
}

data class CalculationMethod(
    val id: Int,
    val nameEn: String,
    val nameAr: String,
    val description: String
)

object CalculationMethods {
    val all = listOf(
        CalculationMethod(4, "Umm Al-Qura, Makkah", "أم القرى - مكة", "السعودية - 18.5° / 90 دقيقة"),
        CalculationMethod(5, "Egyptian General Authority", "الهيئة المصرية", "مصر - 19.5° / 17.5°"),
        CalculationMethod(3, "Muslim World League", "رابطة العالم الإسلامي", "أوروبا وأمريكا - 18° / 17°"),
        CalculationMethod(2, "Islamic Society of North America", "ISNA - أمريكا", "أمريكا الشمالية - 15°"),
        CalculationMethod(1, "University of Karachi", "جامعة كراتشي", "باكستان - 18°"),
        CalculationMethod(8, "Gulf Region", "الخليج", "الخليج - 19.5°"),
        CalculationMethod(12, "Union France (UOIF)", "فرنسا - UOIF", "فرنسا - 12°"),
        CalculationMethod(13, "Diyanet Turkey", "تركيا - ديانة", "تركيا - 18° / 17°"),
        CalculationMethod(16, "Dubai (unofficial)", "دبي", "الإمارات - 18.2°"),
        CalculationMethod(19, "Algeria", "الجزائر", "الجزائر - 18° / 17°"),
        CalculationMethod(20, "Indonesia (Kemenag)", "إندونيسيا", "إندونيسيا - 20° / 18°"),
        CalculationMethod(99, "Custom", "مخصص", "إعدادات يدوية")
    )
}
