package com.kharchabook.ui.screens

import android.content.Context
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
    var showTimePicker by remember { mutableStateOf(false) }
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
                    Row(horizontalArrangement =
