package com.noor.prayertv.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
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
import com.noor.prayertv.data.models.CalculationMethods
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MethodSelectionScreen(
    viewModel: PrayerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val backFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { backFocus.requestFocus() }

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
                    Text(text = "طريقة الحساب  •  Calculation Method", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "اختر الأدق لمنطقتك  •  Aladhan 24 method", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(Modifier.width(100.dp))
            }

            // School selector row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "المذهب للعصر  •  Madhab:", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(end = 8.dp))
                MadhabChip(selected = state.school == 0, labelAr = "شافعي", labelEn = "Shafi", onClick = { viewModel.selectSchool(0) })
                MadhabChip(selected = state.school == 1, labelAr = "حنفي", labelEn = "Hanafi", onClick = { viewModel.selectSchool(1) })
                Spacer(Modifier.weight(1f))
                Text(text = "السابق يغيّر وقت العصر", fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.7f))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(CalculationMethods.all) { method ->
                    val isSelected = method.id == state.methodId
                    Surface(
                        onClick = {
                            viewModel.selectMethod(method.id)
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
                        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
                            containerColor = if (isSelected) BgSurfaceFocused else BgSurface,
                            focusedContainerColor = BgSurfaceFocused
                        ),
                        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
                            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
                            border = if (isSelected) androidx.tv.material3.Border(BorderStroke(2.dp, GoldPrimary.copy(alpha = 0.6f))) else androidx.tv.material3.Border(BorderStroke(1.dp, Color.Transparent))
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${method.nameAr}  •  ${method.nameEn}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(text = method.description, fontSize = 12.sp, color = TextSecondary)
                                Text(text = "ID: ${method.id}", fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.5f))
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
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun MadhabChip(selected: Boolean, labelAr: String, labelEn: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(20.dp)),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = if (selected) GoldPrimary else BgSurfaceFocused,
            focusedContainerColor = GoldPrimary
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
            border = if (selected) androidx.tv.material3.Border(BorderStroke(2.dp, GoldPrimary)) else androidx.tv.material3.Border(BorderStroke(1.dp, Color.Transparent))
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = labelAr, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (selected) Color.Black else TextPrimary)
            Text(text = "• $labelEn", fontSize = 11.sp, color = if (selected) Color.Black.copy(alpha = 0.7f) else TextSecondary)
        }
    }
}
