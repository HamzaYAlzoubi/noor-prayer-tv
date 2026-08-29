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
        City("مكة المكرمة", "Makkah", "السعودية", "Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        City("المدينة المنورة", "Madinah", "السعودية", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh"),
        City("الرياض", "Riyadh", "السعودية", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh"),
        City("جدة", "Jeddah", "السعودية", "Saudi Arabia", 21.5433, 39.1728, "Asia/Riyadh"),
        City("القاهرة", "Cairo", "مصر", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        City("الإسكندرية", "Alexandria", "مصر", "Egypt", 31.2001, 29.9187, "Africa/Cairo"),
        City("دبي", "Dubai", "الإمارات", "UAE", 25.2048, 55.2708, "Asia/Dubai"),
        City("أبوظبي", "Abu Dhabi", "الإمارات", "UAE", 24.4539, 54.3773, "Asia/Dubai"),
        City("الدوحة", "Doha", "قطر", "Qatar", 25.2854, 51.5310, "Asia/Qatar"),
        City("الكويت", "Kuwait City", "الكويت", "Kuwait", 29.3759, 47.9774, "Asia/Kuwait"),
        City("المنامة", "Manama", "البحرين", "Bahrain", 26.2285, 50.5860, "Asia/Bahrain"),
        City("مسقط", "Muscat", "عمان", "Oman", 23.5880, 58.3829, "Asia/Muscat"),
        City("إسطنبول", "Istanbul", "تركيا", "Turkey", 41.0082, 28.9784, "Europe/Istanbul"),
        City("أنقرة", "Ankara", "تركيا", "Turkey", 39.9334, 32.8597, "Europe/Istanbul"),
        City("لندن", "London", "بريطانيا", "UK", 51.5074, -0.1278, "Europe/London"),
        City("باريس", "Paris", "فرنسا", "France", 48.8566, 2.3522, "Europe/Paris"),
        City("برلين", "Berlin", "ألمانيا", "Germany", 52.5200, 13.4050, "Europe/Berlin"),
        City("جاكرتا", "Jakarta", "إندونيسيا", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        City("كوالالمبور", "Kuala Lumpur", "ماليزيا", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        City("الدار البيضاء", "Casablanca", "المغرب", "Morocco", 33.5731, -7.5898, "Africa/Casablanca"),
        City("الجزائر", "Algiers", "الجزائر", "Algeria", 36.7538, 3.0588, "Africa/Algiers"),
        City("تونس", "Tunis", "تونس", "Tunisia", 36.8065, 10.1815, "Africa/Tunis"),
        City("الخرطوم", "Khartoum", "السودان", "Sudan", 15.5007, 32.5599, "Africa/Khartoum"),
        City("نيويورك", "New York", "أمريكا", "USA", 40.7128, -74.0060, "America/New_York")
    )
    val default = all[0]
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
