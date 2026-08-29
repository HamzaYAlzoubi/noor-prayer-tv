package com.noor.prayertv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.GoldMuted
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary

enum class NavType(val ar: String, val en: String, val icon: ImageVector) {
    CITY("المدينة", "City", Icons.Filled.LocationOn),
    CALENDAR("التقويم", "Calendar", Icons.Filled.CalendarMonth),
    METHOD("الحساب", "Method", Icons.Filled.Settings)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvNavCard(
    type: NavType,
    modifier: Modifier = Modifier,
    isFocusedOverride: Boolean? = null,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val effectiveFocused = isFocusedOverride ?: isFocused
    val scale by animateFloatAsState(
        targetValue = if (effectiveFocused) 1.06f else 1f,
        animationSpec = tween(150), label = "navScale"
    )
    val shape = RoundedCornerShape(16.dp)

    Surface(
        modifier = modifier
            .width(280.dp)
            .height(88.dp)
            .scale(scale)
            .shadow(
                elevation = if (effectiveFocused) 12.dp else 0.dp,
                shape = shape,
                ambientColor = GoldPrimary.copy(alpha = 0.35f),
                spotColor = GoldPrimary.copy(alpha = 0.35f)
            )
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
        onClick = onClick,
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = BgSurface,
            focusedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
            border = androidx.tv.material3.Border(BorderStroke(1.dp, GoldMuted.copy(alpha = 0.35f)))
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.06f),
        glow = androidx.tv.material3.ClickableSurfaceDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(GoldPrimary.copy(alpha = 0.55f), 10.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = type.en,
                modifier = Modifier.size(28.dp),
                tint = if (effectiveFocused) GoldPrimary else GoldMuted
            )
            androidx.compose.foundation.layout.Column {
                Text(
                    text = type.ar,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = type.en,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvActionCard(
    titleAr: String,
    titleEn: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = tween(150), label = "actionScale"
    )
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier
            .width(260.dp)
            .height(72.dp)
            .scale(scale)
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
        onClick = onClick,
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = if (isFocused) BgSurfaceFocused else BgSurface,
            focusedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary))
        ),
        scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (isFocused) GoldPrimary else GoldMuted, modifier = Modifier.size(24.dp))
            androidx.compose.foundation.layout.Column {
                Text(titleAr, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(titleEn, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}
