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

    Scaffold(topBar = { TopAppBar(title
