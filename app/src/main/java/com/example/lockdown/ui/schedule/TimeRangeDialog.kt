package com.example.lockdown.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.lockdown.data.BlockRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeDialog(rule: BlockRule, onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val startState = rememberTimePickerState(initialHour = rule.startMinute / 60, initialMinute = rule.startMinute % 60, is24Hour = true)
    val endState = rememberTimePickerState(initialHour = rule.endMinute / 60, initialMinute = rule.endMinute % 60, is24Hour = true)
    var editingStart by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingStart) "Hora de inicio" else "Hora de fin") },
        text = { Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (editingStart) TimePicker(state = startState) else TimePicker(state = endState)
        } },
        confirmButton = {
            if (editingStart) TextButton(onClick = { editingStart = false }) { Text("Siguiente") }
            else TextButton(onClick = { onConfirm(startState.hour * 60 + startState.minute, endState.hour * 60 + endState.minute) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}