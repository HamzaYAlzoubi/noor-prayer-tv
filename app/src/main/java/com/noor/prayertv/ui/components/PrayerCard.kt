package com.noor.prayertv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.compose.material3.Text
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
    onClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1f,
        animationSpec = tween(150), label = "scale"
    )

    // الخلفية: مميزة للصلاة القادمة بدون شريط علوي
    val bg = when {
        isFocused -> BgSurfaceFocused
        prayer.isNext -> Color(0xFF22332D) // أخضر غامق مميز للقادمة
        else -> BgSurface
    }

    // البوردر: دائماً مرئي - الفوكس 3dp ذهبي، القادمة 2dp ذهبي، العادية 1dp واضح (0.35)
    val border = when {
        isFocused -> androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary))
        prayer.isNext -> androidx.tv.material3.Border(BorderStroke(2.dp, GoldPrimary.copy(alpha = 0.95f)))
        else -> androidx.tv.material3.Border(BorderStroke(1.dp, GoldMuted.copy(alpha = 0.35f)))
    }

    val shape = RoundedCornerShape(18.dp)

    Surface(
        modifier = modifier
            .height(152.dp)
            .scale(scale)
            .shadow(
                elevation = if (isFocused) 16.dp else if (prayer.isNext) 6.dp else 0.dp,
                shape = shape,
                ambientColor = GoldPrimary.copy(alpha = if (isFocused) 0.4f else 0.2f),
                spotColor = GoldPrimary.copy(alpha = if (isFocused) 0.4f else 0.2f)
            )
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
        onClick = { onClick?.invoke() },
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = bg,
            focusedContainerColor = BgSurfaceFocused,
            pressedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
            border = border
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(
            focusedScale = 1.06f,
            pressedScale = 1.02f
        ),
        glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.55f), 10.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (prayer.isNext && !isFocused) Brush.verticalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.10f), Color.Transparent)
                    ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // الصف العلوي: حالة الصلاة (القادمة) أو اسم إنجليزي صغير
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (prayer.isNext) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(EmeraldAccent, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = "القادمة • NEXT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = EmeraldAccent,
                                maxLines = 1
                            )
                        }
                    } else {
                        Text(
                            text = prayer.nameEn.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            color = TextSecondary.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // نقطة للصلوات المنقضية
                    if (prayer.isPassed && !prayer.isNext) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(TextSecondary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        )
                    }
                }

                // الوسط: الاسم العربي كبير + الإنجليزي صغير (مرة واحدة فقط - لا تكرار)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = prayer.nameAr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prayer.isNext) GoldPrimary else TextPrimary,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = prayer.nameEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (prayer.isNext) GoldPrimary.copy(alpha = 0.85f) else TextSecondary,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // الأسفل: الوقت بخط كبير وواضح داخل الإطار - لا يخرج أبداً
                Text(
                    text = prayer.time,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (prayer.isNext) GoldPrimary else TextPrimary,
                    letterSpacing = 0.3.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // تظليل خفيف للصلوات المنقضية (بدون إخفاء البوردر)
            if (prayer.isPassed && !prayer.isNext) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgSurface.copy(alpha = 0.18f))
                )
            }
        }
    }
}
