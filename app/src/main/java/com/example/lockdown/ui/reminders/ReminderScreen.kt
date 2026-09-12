package com.example.lockdown.ui.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lockdown.data.Reminder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RemindersScreen(viewModel: RemindersViewModel = viewModel()) {
    val reminders by viewModel.reminders.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Recordatorios", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Sin recordatorios todavía", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderRow(reminder = reminder, onDelete = { viewModel.deleteReminder(reminder) })
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) { Text("+") }
    }

    if (showDialog) {
        ReminderDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, triggerAtMillis, soundUri ->
                viewModel.addReminder(title, triggerAtMillis, soundUri)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ReminderRow(reminder: Reminder, onDelete: () -> Unit) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    ListItem(
        headlineContent = { Text(reminder.title) },
        supportingContent = { Text(formatter.format(Date(reminder.triggerAtMillis))) },
        trailingContent = { TextButton(onClick = onDelete) { Text("Borrar") } }
    )
}