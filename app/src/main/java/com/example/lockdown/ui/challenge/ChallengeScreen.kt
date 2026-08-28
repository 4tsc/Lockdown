package com.example.lockdown.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChallengeScreen(onBack: () -> Unit, viewModel: ChallengeViewModel = viewModel()) {
    val challenge by viewModel.activeChallenge.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Spacer(Modifier.height(16.dp))

        when (val c = challenge) {
            null -> Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Reto de desbloqueo", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Al iniciar, recibirás entre 7 y 8 notificaciones repartidas al azar en las próximas horas. " +
                            "Debes confirmar cada una antes de que expire su plazo. Si pierdes una, el reto falla y las apps siguen bloqueadas.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { viewModel.startChallenge() }) { Text("Comenzar reto") }
            }
            else -> Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Reto en curso", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                // Si tu versión de Material3 marca este parámetro como obsoleto, no pasa nada, sigue funcionando.
                LinearProgressIndicator(progress = c.completedSteps / c.totalSteps.toFloat())
                Spacer(Modifier.height(8.dp))
                Text("${c.completedSteps} de ${c.totalSteps} confirmadas")
                Spacer(Modifier.height(16.dp))
                Text(
                    "Mantén las notificaciones activas. Si pierdes una, tendrás que empezar de nuevo.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}