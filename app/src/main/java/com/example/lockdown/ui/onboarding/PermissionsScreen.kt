package com.example.lockdown.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.lockdown.util.*

@Composable
fun PermissionsScreen(onAllGranted: () -> Unit) {

    val context = LocalContext.current
    BackHandler {
        (context as? android.app.Activity)?.moveTaskToBack(true)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshKey by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshKey++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshKey++ }

    val fullScreenGranted = remember(refreshKey) { canUseFullScreenIntent(context) }
    val notificationGranted = remember(refreshKey) { PermissionChecks.hasNotificationPermission(context) }
    val overlayGranted = remember(refreshKey) { PermissionChecks.canDrawOverlays(context) }
    val exactAlarmGranted = remember(refreshKey) { PermissionChecks.canScheduleExactAlarms(context) }
    val batteryGranted = remember(refreshKey) { PermissionChecks.isIgnoringBatteryOptimizations(context) }
    val accessibilityGranted = remember(refreshKey) { PermissionChecks.isAccessibilityServiceEnabled(context) }
    val deviceAdminGranted = remember(refreshKey) { isDeviceAdminActive(context) }
    val allGranted = notificationGranted && overlayGranted && exactAlarmGranted && batteryGranted && accessibilityGranted && deviceAdminGranted && fullScreenGranted

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Permisos necesarios", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                PermissionCard(
                    "Notificaciones a pantalla completa",
                    "Sin esto, los recordatorios y avisos solo suenan si tocas la notificación primero.",
                    fullScreenGranted,
                    { requestFullScreenIntentPermission(context) }
                )
            }
            item {
                PermissionCard("Notificaciones", "Para avisarte durante el reto de desbloqueo.",
                    notificationGranted, { notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) })
            }
            item {
                PermissionCard("Mostrar sobre otras apps", "Necesario para tapar la pantalla de una app bloqueada.",
                    overlayGranted, { requestOverlayPermission(context) })
            }
            item {
                PermissionCard("Alarmas exactas", "Para que las notificaciones del reto suenen a la hora exacta.",
                    exactAlarmGranted, { requestExactAlarmPermission(context) })
            }
            item {
                PermissionCard("Ignorar optimización de batería", "Evita que el sistema mate la app en segundo plano.",
                    batteryGranted, { requestIgnoreBatteryOptimizations(context) })
            }
            item {
                PermissionCard(
                    "Servicio de accesibilidad",
                    "Es el motor del bloqueo. La primera vez, Android puede mostrarlo bloqueado por ser una app fuera de tienda. " +
                            "Si pasa eso: toca 'Abrir info de la app', luego los tres puntos arriba a la derecha, " +
                            "'Permitir configuración restringida', confirma, y vuelve aquí para activarlo.",
                    accessibilityGranted,
                    { openAccessibilitySettings(context) },
                    secondaryLabel = "Abrir info de la app",
                    onSecondary = { openAppSettings(context) }
                )
            }
            item {
                PermissionCard(
                    "Administrador de dispositivo",
                    "Dificulta desinstalar la app por accidente o impulso. No borra datos ni tiene control total del teléfono.",
                    deviceAdminGranted,
                    { requestDeviceAdmin(context) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onAllGranted, enabled = allGranted, modifier = Modifier.fillMaxWidth()) {
            Text(if (allGranted) "Continuar" else "Falta conceder permisos")
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    onRequest: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (granted) "Ofrecido." else "Entregalo",
                    color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row {
                if (!granted) Button(onClick = onRequest) { Text("Tómalo") }
                if (secondaryLabel != null && onSecondary != null) {
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = onSecondary) { Text(secondaryLabel) }
                }
            }
        }
    }
}