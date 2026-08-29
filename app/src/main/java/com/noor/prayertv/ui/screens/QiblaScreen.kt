package com.noor.prayertv.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun QiblaScreen(
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
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Header with Back - initial focus here
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
                Text(text = "اتجاه القبلة  •  Qibla Direction", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.width(100.dp))
            }

            // Compass card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BgSurface, RoundedCornerShape(24.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = state.city.nameAr + "  •  " + state.city.nameEn,
                        fontSize = 16.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    // Compass visual
                    Box(modifier = Modifier.size(320.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(320.dp)) {
                            val stroke = 2.dp.toPx()
                            drawCircle(color = Color(0xFF2A3A34), style = Stroke(width = stroke))
                            drawCircle(color = GoldPrimary.copy(alpha = 0.3f), radius = size.minDimension / 2 - 20, style = Stroke(width = 1.dp.toPx()))
                            // NSEW markers
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2 - 10
                            // North tick
                            drawLine(
                                color = GoldPrimary,
                                start = Offset(center.x, center.y - radius),
                                end = Offset(center.x, center.y - radius + 18),
                                strokeWidth = 4.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                        // Qibla arrow rotated by direction
                        val direction = state.qiblaDirection ?: 0.0
                        Icon(
                            imageVector = Icons.Filled.Explore,
                            contentDescription = "Qibla",
                            tint = GoldPrimary,
                            modifier = Modifier
                                .size(72.dp)
                                .rotate(direction.toFloat())
                        )
                        // Center dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(GoldPrimary, RoundedCornerShape(7.dp))
                        )
                    }

                    if (state.qiblaDirection != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f°", state.qiblaDirection),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "من الشمال باتجاه عقارب الساعة",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "From North clockwise",
                                fontSize = 11.sp,
                                color = TextSecondary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(text = "جاري حساب اتجاه القبلة...", color = TextSecondary, fontSize = 14.sp)
                    }

                    // Kaaba info
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0F1F1A), RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "الكعبة المشرفة  •  21.4225°N, 39.8262°E",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
