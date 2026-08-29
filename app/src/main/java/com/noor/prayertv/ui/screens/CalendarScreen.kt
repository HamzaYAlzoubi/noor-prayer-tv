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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.noor.prayertv.data.PrayerRepository
import com.noor.prayertv.data.models.TimingData
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: PrayerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val backFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { backFocus.requestFocus() }

    var calendarData by remember { mutableStateOf<List<TimingData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repo = remember { PrayerRepository() }

    LaunchedEffect(state.city, state.methodId, state.school) {
        isLoading = true
        error = null
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        scope.launch {
            val res = repo.getCalendar(year, month, state.city.latitude, state.city.longitude, state.methodId, state.school)
            res.onSuccess {
                calendarData = it.data
                isLoading = false
            }.onFailure {
                error = it.message
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(48.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.focusRequester(backFocus),
                    onClick = onBack,
                    shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
                    colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
                        containerColor = BgSurface,
                        focusedContainerColor = GoldPrimary
                    ),
                    border = androidx.tv.material3.ClickableSurfaceDefaults.border(
                        focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary))
                    ),
                    scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                        Text(text = "رجوع  •  Back", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "التقويم الشهري  •  Monthly Calendar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${state.city.nameAr}  •  ${state.city.nameEn}", fontSize = 12.sp, color = GoldPrimary)
                }
                Spacer(Modifier.width(100.dp))
            }

            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("اليوم", "الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء").forEach {
                    Text(text = it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "جاري تحميل التقويم...  •  Loading calendar...", color = TextSecondary, fontSize = 16.sp)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "خطأ", color = Color(0xFFE57373), fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(calendarData) { data ->
                        val isToday = data.date.gregorian.day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isToday) GoldPrimary.copy(alpha = 0.15f) else BgSurface,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = data.date.gregorian.day.padStart(2,'0') + "  " + data.date.hijri.day, fontSize = 13.sp, color = if (isToday) GoldPrimary else TextPrimary, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.fajr, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.sunrise, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.dhuhr, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.asr, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.maghrib, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(text = data.timings.isha, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
