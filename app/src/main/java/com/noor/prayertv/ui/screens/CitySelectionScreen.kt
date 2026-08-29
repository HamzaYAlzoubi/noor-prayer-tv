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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noor.prayertv.data.models.Cities
import com.noor.prayertv.data.models.City
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.BgSurface
import com.noor.prayertv.ui.theme.BgSurfaceFocused
import com.noor.prayertv.ui.theme.GoldPrimary
import com.noor.prayertv.ui.theme.TextPrimary
import com.noor.prayertv.ui.theme.TextSecondary
import com.noor.prayertv.viewmodel.PrayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

// Nominatim models for remote suggestions
private data class NominatimAddress(
    val country: String? = null
)

private data class NominatimPlace(
    val display_name: String? = null,
    val lat: String = "0",
    val lon: String = "0",
    val address: NominatimAddress? = null,
    val name: String? = null
)

private suspend fun fetchNominatimSuggestions(query: String): List<City> = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&limit=5&accept-language=ar"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "NoorPrayerTV/1.0 (Android TV; contact: noor@example.com)")
            .header("Accept", "application/json")
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext emptyList<City>()
        val body = response.body?.string() ?: return@withContext emptyList()
        if (body.isBlank() || body == "[]") return@withContext emptyList()
        val gson = Gson()
        val type = object : TypeToken<List<NominatimPlace>>() {}.type
        val places: List<NominatimPlace> = try {
            gson.fromJson(body, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        places.mapNotNull { place ->
            val lat = place.lat.toDoubleOrNull() ?: return@mapNotNull null
            val lon = place.lon.toDoubleOrNull() ?: return@mapNotNull null
            if (lat == 0.0 && lon == 0.0) return@mapNotNull null
            val display = place.display_name ?: return@mapNotNull null
            val firstPart = display.split(",").firstOrNull()?.trim()?.takeIf { it.isNotEmpty() } ?: display
            val country = place.address?.country
                ?: display.split(",").lastOrNull()?.trim()?.takeIf { it.isNotEmpty() }
                ?: "العالم"
            City(
                nameAr = place.name ?: firstPart,
                nameEn = place.name ?: firstPart,
                countryAr = country,
                countryEn = country,
                latitude = lat,
                longitude = lon,
                timeZone = "UTC"
            )
        }.take(5)
    } catch (e: Exception) {
        emptyList()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CitySelectionScreen(
    viewModel: PrayerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val backFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf("") }
    var remoteResults by remember { mutableStateOf<List<City>>(emptyList()) }
    var isLoadingRemote by remember { mutableStateOf(false) }
    var isSearchFocused by remember { mutableStateOf(false) }

    // Local instant filter (instant suggestions)
    val localFiltered = remember(searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank() || q.length < 2) emptyList()
        else Cities.all.filter {
            it.nameAr.contains(q) ||
                it.nameEn.contains(q, ignoreCase = true) ||
                it.countryAr.contains(q) ||
                it.countryEn.contains(q, ignoreCase = true)
        }.take(5)
    }

    // Remote fetch with debounce 500ms
    LaunchedEffect(searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank() || q.length < 2) {
            remoteResults = emptyList()
            isLoadingRemote = false
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(500)
        // check again after delay (query may have changed)
        if (searchQuery.trim() != q) return@LaunchedEffect
        isLoadingRemote = true
        val remote = fetchNominatimSuggestions(q)
        // only update if query still same
        if (searchQuery.trim() == q) {
            remoteResults = remote
        }
        isLoadingRemote = false
    }

    // Combined suggestions max 5: local first, then remote deduped
    val suggestions = remember(localFiltered, remoteResults) {
        val combined = mutableListOf<City>()
        combined.addAll(localFiltered)
        for (r in remoteResults) {
            if (combined.size >= 5) break
            val isDup = combined.any {
                kotlin.math.abs(it.latitude - r.latitude) < 0.01 &&
                    kotlin.math.abs(it.longitude - r.longitude) < 0.01
            }
            if (!isDup) combined.add(r)
        }
        // If no local but has remote, show remote only
        if (combined.isEmpty() && remoteResults.isNotEmpty()) {
            remoteResults.take(5)
        } else {
            combined.take(5)
        }
    }

    // Find selected index for existing Arab list highlight
    val selectedIndex = Cities.all.indexOfFirst { it.latitude == state.city.latitude && it.longitude == state.city.longitude }

    LaunchedEffect(Unit) {
        // Keep Back button initial focus as requested; search field is second focusable element
        kotlinx.coroutines.delay(100)
        backFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(48.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Top bar: Back + Title
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
                    Text(text = "اختر مدينتك  •  Select City", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${Cities.all.size} مدينة  •  Aladhan API دقيق حتى 1 دقيقة", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(Modifier.width(100.dp))
            }

            // Search bar - D-Pad focusable second element with clear border
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(searchFocus)
                    .onFocusChanged { isSearchFocused = it.isFocused },
                placeholder = {
                    Text(
                        text = "ابحث عن مدينتك • Search city (English / العربية)  —  مثال: Cairo, باريس, Tokyo",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = if (isSearchFocused) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (isLoadingRemote) {
                        Text(text = "…", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                    } else if (searchQuery.isNotEmpty()) {
                        // simple clear indicator (focusable via click not needed for TV, but keep visual)
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = GoldPrimary.copy(alpha = 0.3f),
                    focusedContainerColor = BgSurfaceFocused,
                    unfocusedContainerColor = BgSurface,
                    cursorColor = GoldPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )

            // Suggestions dropdown (max 5) - Tv Surface with 3dp Gold focused border
            if (searchQuery.trim().length >= 2) {
                if (suggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "نتائج البحث • Search Results (${suggestions.size})",
                            fontSize = 12.sp,
                            color = GoldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Use LazyColumn with fixed height for max 5 items
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suggestions) { city ->
                                SuggestionRow(
                                    cityAr = city.nameAr,
                                    cityEn = city.nameEn,
                                    country = "${city.countryAr} • ${city.countryEn}",
                                    latLon = String.format("%.4f, %.4f", city.latitude, city.longitude),
                                    onClick = {
                                        viewModel.selectCustomCity(city)
                                        onBack()
                                    }
                                )
                            }
                        }
                    }
                } else if (!isLoadingRemote && localFiltered.isEmpty() && remoteResults.isEmpty()) {
                    // No results fallback - show local filter only message or empty state
                    if (searchQuery.trim().length >= 2) {
                        Text(
                            text = "لا توجد نتائج محلية • جاري البحث عبر الإنترنت...",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Header for Arab cities list - keep existing easy list
            Text(
                text = "الدول العربية - اختر سريعاً • 42 مدينة",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )

            // Existing Arab cities LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
private fun SuggestionRow(
    cityAr: String,
    cityEn: String,
    country: String,
    latLon: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = BgSurface,
            focusedContainerColor = BgSurfaceFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(BorderStroke(3.dp, GoldPrimary)),
            border = androidx.tv.material3.Border(BorderStroke(1.dp, Color.Transparent))
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
                Text(text = "$cityAr  •  $cityEn", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
                Text(text = country, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = latLon, fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.7f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                    Text(text = "اختر • Select", fontSize = 10.sp, color = GoldPrimary)
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
