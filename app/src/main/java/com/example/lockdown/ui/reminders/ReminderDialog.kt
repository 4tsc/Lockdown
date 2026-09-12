package com.example.lockdown.ui.reminders

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, triggerAtMillis: Long, soundUri: String?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedSoundUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSoundLabel by remember { mutableStateOf("Sonido por defecto") }
    var step by remember { mutableStateOf(0) } // 0 = título+sonido, 1 = fecha, 2 = hora
    val timeState = rememberTimePickerState(is24Hour = true)
    val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    val soundPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            selectedSoundUri = uri
            selectedSoundLabel = if (uri != null) {
                RingtoneManager.getRingtone(context, uri)?.getTitle(context) ?: "Sonido elegido"
            } else "Sonido por defecto"
        }
    }

    fun launchSoundPicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedSoundUri)
        }
        soundPickerLauncher.launch(intent)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo recordatorio") },
        text = {
            when (step) {
                0 -> Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("¿Qué debes recordar?") })
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { launchSoundPicker() }) { Text(selectedSoundLabel) }
                }
                1 -> DatePicker(state = dateState)
                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) { TimeInput(state = timeState) }
            }
        },
        confirmButton = {
            when (step) {
                0 -> TextButton(onClick = { if (title.isNotBlank()) step = 1 }, enabled = title.isNotBlank()) { Text("Siguiente") }
                1 -> TextButton(onClick = {
                    // el DatePicker de Material3 trabaja internamente en UTC; hay que
                    // extraer la fecha con ese huso para no correr un día por accidente
                    dateState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    step = 2
                }) { Text("Siguiente") }
                else -> TextButton(onClick = {
                    val dateTime = selectedDate.atTime(LocalTime.of(timeState.hour, timeState.minute))
                    val triggerAtMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    onConfirm(title, triggerAtMillis, null)
                }) { Text("Guardar") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}