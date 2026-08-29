# نور - مواقيت الصلاة للشاشة | Noor Prayer TV

تطبيق Android TV يعرض مواقيت الصلاة بدقة مطلقة عبر **Aladhan API** (الأدق عالمياً - 24 طريقة حساب، بدون مفتاح API، دقة ±1 دقيقة).

## لماذا Aladhan ؟ (نتيجة بحث الوكلاء)

- **الأدق**: الوحيد الذي يدعم `tune` لكل صلاة + `method=99` مخصص لمطابقة المساجد المحلية بدقة 0 دقيقة
- **24 طريقة**: أم القرى (4) 🇸🇦، المصرية (5) 🇪🇬، رابطة العالم (3) 🌍، ISNA، كراتشي، دبي، الجزائر، تونس، المغرب، فرنسا، تركيا، ماليزيا...
- **بلا حدود**: بلا مفتاح، 12 طلب/ثانية، CORS مفتوح، cache ساعة، يعمل حتى بدون تسجيل
- **شهر في طلب واحد**: `GET /v1/calendar/2026/8` يعطي 31 يوم - مثالي للتلفاز ضعيف الموارد
- **هجري + قبلة**: نفس الـ base URL للقبلة وتحويل التاريخ

> البديل IslamicFinder **ليس له API عام مجاني** - مجرد واجهة ويب، المطورون يلجؤون للسحب (scraping) غير المستقر.

## المميزات
- واجهة 10-foot للتلفاز: نصوص كبيرة، مسافات واسعة، صفوف أفقية (نمط Netflix/YouTube)
- تنقل كامل بالريموت (D-Pad): Up/Down/Left/Right + OK + Back - طبقاً لـ `android-tv-dpad-navigation` skill
- عد تنازلي حي للصلاة القادمة + حلقة تقدم + تظليل للصلوات المنتهية
- 24 مدينة جاهزة (مكة، المدينة، الرياض، القاهرة، دبي، إسطنبول، لندن، باريس، جاكرتا...) + حفظ عبر DataStore
- اختيار طريقة الحساب والمذهب (شافعي/حنفي للعصر)
- شاشات: الرئيسية، القبلة (بوصلة + زاوية من API)، التقويم الشهري (شبكة شهر كامل)
- لغة عربية/إنجليزية RTL-aware
- هوامش آمنة 48dp ضد قص الشاشة (Overscan)

## التقنية
- Kotlin 1.9.22 + Compose for TV (`tv-material:1.0.1`, `tv-foundation:1.0.0-alpha11`)
- Retrofit + OkHttp + Gson للـ API
- DataStore Preferences
- MVVM (ViewModel + StateFlow + ticker 1s)

## البناء عبر GitHub Actions

كل دفع إلى `main` يبني APK تلقائياً:

```yaml
# .github/workflows/build.yml
- Setup JDK 17 + Android SDK + Gradle 8.7
- gradle assembleDebug (أو ./gradlew إذا وجد)
- رفع APK كـ artifact: noor-prayer-tv-debug-apk
```

تحميل الـ APK من تبويب **Actions → Build Noor Prayer TV APK → Artifacts**.

## التشغيل محلياً (اختياري)
```bash
# يتطلب Android Studio Hedgehog+ و JDK 17
gradle assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## D-Pad قواعد محققة
1. **تركيز أولي**: `NextPrayerHero` يأخذ التركيز فور الإقلاع (`FocusRequester.requestFocus()`)
2. **عنصر واحد فقط**: كل صف يستخدم `focusRestorer()` لتذكر آخر عنصر
3. **مرئي بوضوح**: Scale 1.08 + حد ذهبي 3dp + توهج + خلفية داكنة عند التركيز
4. **OK للتفعيل / Back للرجوع**: `BackHandler` منفصل
5. **10-foot**: بطاقات 274×148dp، نص 28sp للوقت، عد تنازلي 36sp

## API مثال
```bash
curl "https://api.aladhan.com/v1/timings/29-08-2026?latitude=21.4225&longitude=39.8262&method=4&school=0"
curl "https://api.aladhan.com/v1/calendar/2026/8?latitude=30.0444&longitude=31.2357&method=5"
curl "https://api.aladhan.com/v1/qibla/21.4225/39.8262"
```

صُنع بـ `Compose for TV` + `Aladhan` لإضاءة شاشات المنازل بمواقيت لا تخطئ دقيقة.
