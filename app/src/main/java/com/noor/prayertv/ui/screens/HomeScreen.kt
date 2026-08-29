package com.noor.prayertv.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.noor.prayertv.ui.components.NavType
import com.noor.prayertv.ui.components.NextPrayerHero
import com.noor.prayertv.ui.components.PrayerCard
import com.noor.prayertv.ui.components.TvNavCard
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: PrayerViewModel,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val heroFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        heroFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(48.dp)
    ) {
        // خلفية متدرجة خفيفة
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.06f), Color.Transparent)
                    ),
                    RoundedCornerShape(24.dp)
                )
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // TopBar - المدينة قابلة للضغط مباشرة
            TopBar(
                cityNameAr = state.city.nameAr,
                cityNameEn = state.city.nameEn,
                hijri = state.hijriDateAr.ifEmpty { state.hijriDate },
                gregorian = state.gregorianDate,
                currentTime = state.currentTime,
                onCityClick = { onNavigate("city") }
            )

            NextPrayerHero(
                nextPrayer = state.nextPrayer,
                countdown = state.nextPrayerCountdown,
                cityName = state.city.nameAr,
                hijriDate = state.hijriDateAr.ifEmpty { state.hijriDate },
                currentTime = state.currentTime,
                modifier = Modifier,
                focusRequester = heroFocusRequester,
                onClick = { }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صلوات اليوم  •  5 فروض  •  ${state.gregorianDate.substringBefore(",")}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = GoldPrimary)
                } else {
                    Text(
                        text = viewModel.getMethodName(),
                        fontSize = 12.sp,
                        color = GoldPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            // PrayerRow - 5 بطاقات فقط (بدون شروق) - تصميم جديد لا يقطع الوقت
            if (state.error != null && state.prayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error ?: "خطأ", color = Color(0xFFE57373), fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "اضغط OK لإعادة المحاولة", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    items(state.prayers) { prayer ->
                        PrayerCard(
                            prayer = prayer,
                            onClick = { }
                        )
                    }
                }
            }

            // NavRow - 3 أزرار فقط: المدينة أولاً (الأهم) + التقويم + الحساب - لا قبلة
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
            ) {
                item {
                    TvNavCard(type = NavType.CITY, onClick = { onNavigate("city") })
                }
                item {
                    TvNavCard(type = NavType.CALENDAR, onClick = { onNavigate("calendar") })
                }
                item {
                    TvNavCard(type = NavType.METHOD, onClick = { onNavigate("method") })
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "استخدم الريموت للتنقل  •  OK للاختيار  •  BACK للرجوع  •  اضغط على المدينة لتغييرها",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopBar(
    cityNameAr: String,
    cityNameEn: String,
    hijri: String,
    gregorian: String,
    currentTime: String,
    onCityClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(Color(0xFF16201C), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Mosque,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = "نور  •  NOOR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = hijri.ifEmpty { "جاري التحميل..." },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFE9A3)
                )
                Text(
                    text = gregorian.ifEmpty { currentTime },
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // City pill - قابل للفوكس بوضوح: بوردر 3dp عند الفوكس + إيحاء أنه زر
        Surface(
            onClick = onCityClick,
            shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
            colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
                containerColor = BgSurface,
                focusedContainerColor = BgSurfaceFocused
            ),
            border = androidx.tv.material3.ClickableSurfaceDefaults.border(
                focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
                border = androidx.tv.material3.Border(BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)))
            ),
            scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
            glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
                focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.5f), 10.dp)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$cityNameAr • $cityNameEn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$currentTime  •  اضغط OK للتغيير",
                        fontSize = 11.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
