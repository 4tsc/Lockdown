package com.example.lockdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.lockdown.ui.onboarding.PermissionsScreen
import com.example.lockdown.ui.onboarding.WelcomeScreen
import com.example.lockdown.util.Prefs
import com.example.lockdown.ui.theme.LockdownTheme
import com.example.lockdown.ui.schedule.ScheduleScreen

sealed class Screen {
    object Welcome : Screen()
    object Permissions : Screen()
    object Home : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LockdownTheme {
                val context = LocalContext.current
                var screen by remember {
                    mutableStateOf<Screen>(if (Prefs.isOnboardingDone(context)) Screen.Home else Screen.Welcome)
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        is Screen.Welcome -> WelcomeScreen(onContinue = { screen = Screen.Permissions })
                        is Screen.Permissions -> PermissionsScreen(onAllGranted = {
                            Prefs.setOnboardingDone(context, true)
                            screen = Screen.Home
                        })
                        is Screen.Home -> ScheduleScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Fase 3: aquí irá el editor de horarios")
    }
}