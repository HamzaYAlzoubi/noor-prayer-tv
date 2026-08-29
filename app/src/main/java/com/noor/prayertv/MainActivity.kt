package com.noor.prayertv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noor.prayertv.ui.screens.CalendarScreen
import com.noor.prayertv.ui.screens.CitySelectionScreen
import com.noor.prayertv.ui.screens.HomeScreen
import com.noor.prayertv.ui.screens.MethodSelectionScreen
import com.noor.prayertv.ui.screens.QiblaScreen
import com.noor.prayertv.ui.theme.BgPrimary
import com.noor.prayertv.ui.theme.NoorPrayerTvTheme
import com.noor.prayertv.viewmodel.PrayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoorPrayerTvTheme {
                PrayerTvApp()
            }
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object Qibla : Screen()
    data object Calendar : Screen()
    data object City : Screen()
    data object Method : Screen()
}

@Composable
fun PrayerTvApp() {
    val viewModel: PrayerViewModel = viewModel()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // Back handling - Rule #4: Back is separate concern, returns to home with focus restored
    BackHandler(enabled = currentScreen != Screen.Home) {
        currentScreen = Screen.Home
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        when (currentScreen) {
            is Screen.Home -> HomeScreen(
                viewModel = viewModel,
                onNavigate = { dest ->
                    currentScreen = when (dest) {
                        "qibla" -> Screen.Qibla
                        "calendar" -> Screen.Calendar
                        "city" -> Screen.City
                        "method" -> Screen.Method
                        else -> Screen.Home
                    }
                }
            )
            is Screen.Qibla -> QiblaScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
            is Screen.Calendar -> CalendarScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
            is Screen.City -> CitySelectionScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
            is Screen.Method -> MethodSelectionScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
        }
    }
}
