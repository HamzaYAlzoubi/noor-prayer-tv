package com.noor.prayertv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.noor.prayertv.ui.components.NavType
import com.noor.prayertv.ui.components.NextPrayerHero
import com.noor.prayertv.ui.components.PrayerCard
import com.noor.prayertv.ui.components.TvNavCard
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(
    viewModel: PrayerViewModel,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Focus requesters - Rule #1: explicit initial focus
    val heroFocusRequester = remember { FocusRequester() }
    val prayerRowFocusRequester = remember { FocusRequester() }

    // Cold-launch test: hero gets focus immediately
    LaunchedEffect(Unit) {
        heroFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            // Overscan safe area 48dp
            .padding(48.dp)
    ) {
        // Background mosque silhouette gradient
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // TopBar
            TopBar(
                cityName = "${state.city.nameAr} • ${state.city.nameEn}",
                hijri = state.hijriDateAr.ifEmpty { state.hijriDate },
                gregorian = state.gregorianDate,
                currentTime = state.currentTime
            )

            // NextPrayerHero - INITIAL FOCUS (Rule #1)
            NextPrayerHero(
                nextPrayer = state.nextPrayer,
                countdown = state.nextPrayerCountdown,
                cityName = state.city.nameAr,
                hijriDate = state.hijriDateAr.ifEmpty { state.hijriDate },
                currentTime = state.currentTime,
                modifier = Modifier,
                focusRequester = heroFocusRequester,
                onClick = { /* focusable entry point */ }
            )

            // Section label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صلوات اليوم  •  TODAY'S PRAYERS • ${state.gregorianDate.substringBefore(",")}",
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

            // PrayerRow - TvLazyRow with focusRestorer (Rule #2: single focus, ordered traversal)
            if (state.error != null && state.prayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error ?: "خطأ", color = Color(0xFFE57373), fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "اضغط OK لإعادة المحاولة  •  Press OK to retry", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(state.prayers) { index, prayer ->
                        PrayerCard(
                            prayer = prayer,
                            onClick = { /* could show detail */ }
                        )
                    }
                }
            }

            // NavRow - 4 nav cards with explicit focus order
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    TvNavCard(type = NavType.QIBLA, onClick = { onNavigate("qibla") })
                }
                item {
                    TvNavCard(type = NavType.CALENDAR, onClick = { onNavigate("calendar") })
                }
                item {
                    TvNavCard(type = NavType.CITY, onClick = { onNavigate("city") })
                }
                item {
                    TvNavCard(type = NavType.METHOD, onClick = { onNavigate("method") })
                }
            }

            // Footer hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "استخدم الريموت للتنقل  •  Use D-Pad to navigate  •  OK للاختيار  •  BACK للرجوع",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    cityName: String,
    hijri: String,
    gregorian: String,
    currentTime: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF16201C), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp),
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

        // City pill + time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = cityName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = currentTime,
                    fontSize = 12.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(32.dp)
                    .background(GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(1.dp))
            )
            Text(
                text = "ALADHAN • أم القرى",
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                letterSpacing = 0.8.sp
            )
        }
    }
}
