package com.noor.prayertv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.noor.prayertv.data.models.PrayerInfo
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.EmeraldAccent
import com.noor.prayertv.ui.theme.GoldMuted
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PrayerCard(
    prayer: PrayerInfo,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onClick: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1f,
        animationSpec = tween(150), label = "scale"
    )

    val bg = when {
        isFocused -> BgSurfaceFocused
        prayer.isNext -> BgSurfaceFocused
        else -> BgSurface
    }

    val border = when {
        isFocused -> BorderStroke(3.dp, GoldPrimary)
        prayer.isNext -> BorderStroke(2.dp, GoldPrimary.copy(alpha = 0.9f))
        else -> BorderStroke(1.dp, GoldMuted.copy(alpha = 0.2f))
    }

    val shape = RoundedCornerShape(20.dp)

    var mod = modifier
        .width(274.dp)
        .height(148.dp)
        .scale(scale)
        .shadow(
            elevation = if (isFocused) 16.dp else 0.dp,
            shape = shape,
            ambientColor = GoldPrimary.copy(alpha = 0.35f),
            spotColor = GoldPrimary.copy(alpha = 0.35f)
        )
        .clip(shape)
        .onFocusChanged {
            isFocused = it.isFocused
            onFocusChanged?.invoke(it.isFocused)
        }

    if (focusRequester != null) mod = mod.focusRequester(focusRequester)

    Surface(
        modifier = mod,
        onClick = { onClick?.invoke() },
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = bg,
            focusedContainerColor = BgSurfaceFocused,
            pressedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = BorderStroke(3.dp, GoldPrimary),
            border = border
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(
            focusedScale = 1.08f,
            pressedScale = 1.02f
        ),
        glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.6f), 12.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (prayer.isNext && !isFocused) Brush.verticalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.12f), Color.Transparent)
                    ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prayer.nameEn.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.2.sp,
                        color = if (prayer.isNext) GoldPrimary else TextSecondary
                    )
                    if (prayer.isPassed) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(TextSecondary.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        )
                    } else if (prayer.isNext) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(EmeraldAccent, RoundedCornerShape(5.dp))
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column {
                    Text(
                        text = prayer.nameAr,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = prayer.nameEn,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = prayer.time,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prayer.isNext) GoldPrimary else TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    if (prayer.isNext && !isFocused) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "●",
                            fontSize = 10.sp,
                            color = EmeraldAccent,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
            // Top indicator for next
            if (prayer.isNext) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(EmeraldAccent, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .align(Alignment.TopCenter)
                )
            }
            // dim passed
            if (prayer.isPassed) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(BgSurface.copy(alpha = 0.35f))
                )
            }
        }
    }
}
