package com.example.lockdown.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lockdown.ui.common.UnlockCodeCard

@Composable
fun LockedScreen(onUnlocked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bloqueo activo", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(80.dp))
        UnlockCodeCard(onUnlocked = onUnlocked)
    }
}