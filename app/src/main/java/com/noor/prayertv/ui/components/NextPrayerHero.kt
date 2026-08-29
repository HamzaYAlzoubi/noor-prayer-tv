package com.noor.prayertv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.compose.material3.Text
import com.noor.prayertv.data.models.PrayerInfo
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.EmeraldAccent
import com.noor.prayertv.ui.theme.EmeraldDeep
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NextPrayerHero(
    nextPrayer: PrayerInfo?,
    countdown: String,
    cityName: String,
    hijriDate: String,
    currentTime: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = tween(150), label = "heroScale"
    )

    val shape = RoundedCornerShape(24.dp)
    var mod = modifier
        .fillMaxWidth()
        .height(300.dp)
        .scale(scale)
        .shadow(
            elevation = if (isFocused) 20.dp else 8.dp,
            shape = shape,
            ambientColor = GoldPrimary.copy(alpha = if (isFocused) 0.4f else 0.15f),
            spotColor = GoldPrimary.copy(alpha = if (isFocused) 0.4f else 0.15f)
        )
        .clip(shape)
        .onFocusChanged { isFocused = it.isFocused }

    if (focusRequester != null) mod = mod.focusRequester(focusRequester)

    val gradient = Brush.horizontalGradient(
        listOf(GoldPrimary.copy(alpha = 0.18f), EmeraldDeep.copy(alpha = 0.95f))
    )

    Surface(
        modifier = mod,
        onClick = { onClick?.invoke() },
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = Color(0xFF16201C),
            focusedContainerColor = Color(0xFF1E2E28)
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = BorderStroke(3.dp, GoldPrimary),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.25f))
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.5f), 16.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left block
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الصلاة القادمة  •  NEXT PRAYER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    if (nextPrayer != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = nextPrayer.nameAr,
                                fontSize = 46.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = nextPrayer.nameEn,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = GoldPrimary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${nextPrayer.time}  •  $cityName",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "جاري التحميل...",
                            fontSize = 28.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    // Countdown
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = countdown,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "متبقي",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Right block - progress + time + hijri
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Circular progress placeholder (68% example)
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            val stroke = 8.dp.toPx()
                            drawArc(
                                color = Color(0xFF1E2E28),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                                size = Size(size.width - stroke, size.height - stroke),
                                topLeft = androidx.compose.ui.geometry.Offset(stroke/2, stroke/2)
                            )
                            drawArc(
                                color = EmeraldAccent,
                                startAngle = -90f,
                                sweepAngle = 220f, // dynamic later
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                                size = Size(size.width - stroke, size.height - stroke),
                                topLeft = androidx.compose.ui.geometry.Offset(stroke/2, stroke/2)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentTime.substringBeforeLast(":"),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "الآن",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = hijriDate,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFE9A3)
                    )
                }
            }

            // Subtle mosque silhouette hint at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(width = 200.dp, height = 40.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, GoldPrimary.copy(alpha = 0.06f))
                        ),
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}
