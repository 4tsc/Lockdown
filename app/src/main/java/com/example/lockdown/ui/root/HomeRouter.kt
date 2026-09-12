package com.example.lockdown.ui.root

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.lockdown.ui.lock.LockedScreen
import com.example.lockdown.ui.schedule.ScheduleScreen
import com.example.lockdown.util.Prefs

@Composable
fun HomeRouter() {
    val context = LocalContext.current
    var locked by remember { mutableStateOf(Prefs.isSetupLocked(context)) }
    var unlockRefresh by remember { mutableStateOf(0) }
    val unlockedToday = remember(unlockRefresh) { Prefs.isUnlockedToday(context) }

    if (!locked || unlockedToday) {
        ScheduleScreen(onFinishSetup = {
            Prefs.setSetupLocked(context)
            locked = true
        })
    } else {
        LockedScreen(onUnlocked = { unlockRefresh++ })
    }
}