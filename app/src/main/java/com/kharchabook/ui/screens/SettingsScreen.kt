package com.kharchabook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kharchabook.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: AppViewModel) {
    val ctx = LocalContext.current
    var cycleDay by remember { mutableStateOf(vm.cycleStartDay) }
    var reminderOn by remember { mutableStateOf(vm.reminderEnabled) }
    var hour by remember { mutableStateOf(vm.reminderHour) }
    var minute by remember { mutableStateOf(vm.reminderMinute) }
    var showDayPicker by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings", fontWeight = FontWeight.SemiBold) }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Salary cycle", style = MaterialTheme.typography.titleMedium)
                    Text("Cycle starts on day $cycleDay of each month.", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = { showDayPicker = true }) { Text("Change start day") }
                }
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Daily reminder", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Switch(
                            checked = reminderOn,
                            onCheckedChange = {
                                reminderOn = it
                                vm.setReminder(it, hour, minute, ctx)
                            }
                        )
                    }
                    Text("Reminds you daily to log expenses.", style = MaterialTheme.typography.bodySmall)
                    
                    if (reminderOn) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text(
                                "Time: ${if (hour % 12 == 0) 12 else hour % 12}:${minute.toString().padStart(2, '0')} ${if (hour >= 12) "PM" else "AM"}",
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                // Simple time cycling for ease of use
                                hour = (hour + 1) % 24
                                vm.setReminder(reminderOn, hour, minute, ctx)
                            }) {
                                Text("+1 Hour")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDayPicker) {
        AlertDialog(
            onDismissRequest = { showDayPicker = false },
            title = { Text("Cycle Start Day") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select the day of the month your salary cycle starts (1-28).")
                    OutlinedTextField(
                        value = cycleDay.toString(),
                        onValueChange = { s ->
                            val d = s.toIntOrNull()
                            if (d != null && d in 1..28) cycleDay = d
                        },
                        label = { Text("Day (1-28)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.setCycleStartDay(cycleDay)
                    showDayPicker = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDayPicker = false }) { Text("Cancel") } }
        )
    }
}
