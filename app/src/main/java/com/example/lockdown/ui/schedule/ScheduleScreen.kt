package com.example.lockdown.ui.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lockdown.data.BlockRule
import com.example.lockdown.data.InstalledApp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

private val dayLabels = DayOfWeek.entries.associateWith {
    it.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { c -> c.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val rules by viewModel.rulesForSelectedDay.collectAsState()
    var editingRule by remember { mutableStateOf<BlockRule?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Horarios de bloqueo", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp)) {
            DayOfWeek.entries.forEach { day ->
                FilterChip(
                    selected = day.value == selectedDay,
                    onClick = { viewModel.selectDay(day.value) },
                    label = { Text(dayLabels[day] ?: day.name) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.installedApps, key = { it.packageName }) { app ->
                val rule = rules.find { it.packageName == app.packageName }
                AppRow(
                    app = app,
                    rule = rule,
                    onToggle = { enabled -> viewModel.toggleApp(app.packageName, enabled) },
                    onEditTime = { rule?.let { editingRule = it } }
                )
            }
        }
    }

    editingRule?.let { rule ->
        TimeRangeDialog(
            rule = rule,
            onDismiss = { editingRule = null },
            onConfirm = { start, end ->
                viewModel.updateTimeRange(rule, start, end)
                editingRule = null
            }
        )
    }
}

@Composable
private fun AppRow(app: InstalledApp, rule: BlockRule?, onToggle: (Boolean) -> Unit, onEditTime: () -> Unit) {
    val bitmap = remember(app.packageName) { app.icon.toBitmap().asImageBitmap() }
    ListItem(
        headlineContent = { Text(app.label) },
        supportingContent = {
            Text(if (rule?.isEnabled == true) "${fmt(rule.startMinute)} - ${fmt(rule.endMinute)}" else "Sin bloquear")
        },
        leadingContent = { Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(40.dp)) },
        trailingContent = {
            Row {
                if (rule?.isEnabled == true) TextButton(onClick = onEditTime) { Text("Horario") }
                Switch(checked = rule?.isEnabled == true, onCheckedChange = onToggle)
            }
        }
    )
}

private fun fmt(minutes: Int) = "%02d:%02d".format(minutes / 60, minutes % 60)