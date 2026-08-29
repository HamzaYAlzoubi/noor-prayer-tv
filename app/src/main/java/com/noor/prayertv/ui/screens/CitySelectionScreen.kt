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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.noor.prayertv.data.models.Cities
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CitySelectionScreen(
    viewModel: PrayerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val firstFocus = remember { FocusRequester() }
    val backFocus = remember { FocusRequester() }

    // Find selected index
    val selectedIndex = Cities.all.indexOfFirst { it.latitude == state.city.latitude && it.longitude == state.city.longitude }

    LaunchedEffect(Unit) {
        // Try to focus selected city, fallback to back
        if (selectedIndex >= 0) {
            // small delay to let list compose
            kotlinx.coroutines.delay(100)
            // We can't directly request focus on Lazy item, so focus back as initial and let user navigate down
            backFocus.requestFocus()
        } else {
            backFocus.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(48.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
                        focusedBorder = BorderStroke(3.dp, GoldPrimary)
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
                    Text(text = "اختر مدينتك  •  Select City", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${Cities.all.size} مدينة  •  Aladhan API دقيق حتى 1 دقيقة", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(Modifier.width(100.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(Cities.all) { index, city ->
                    val isSelected = index == selectedIndex
                    CityRow(
                        cityAr = city.nameAr,
                        cityEn = city.nameEn,
                        country = "${city.countryAr} • ${city.countryEn}",
                        isSelected = isSelected,
                        onClick = {
                            viewModel.selectCity(index)
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CityRow(
    cityAr: String,
    cityEn: String,
    country: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) BgSurfaceFocused else BgSurface,
            focusedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = BorderStroke(3.dp, GoldPrimary),
            border = if (isSelected) BorderStroke(2.dp, GoldPrimary.copy(alpha = 0.6f)) else BorderStroke(1.dp, Color.Transparent)
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.4f), 8.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "$cityAr  •  $cityEn", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = country, fontSize = 12.sp, color = TextSecondary)
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(GoldPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Selected", tint = Color.Black, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
