package com.kharchabook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kharchabook.data.ExpenseRow
import com.kharchabook.ui.AppViewModel
import com.kharchabook.util.CycleUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditScreen(
    vm: AppViewModel,
    existing: ExpenseRow?,
    onBack: () -> Unit
) {
    val categories by vm.categories.collectAsState()
    var amountText by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var categoryId by remember {
        mutableStateOf(existing?.categoryId ?: categories.firstOrNull()?.id)
    }
    var timestamp by remember { mutableStateOf(existing?.timestamp ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (categoryId == null) categoryId = categories.firstOrNull()?.id
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Add expense" else "Edit expense", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = { s -> amountText = s.filter { it.isDigit() }.take(9) },
                label = { Text("Amount (PKR)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("Category", style = MaterialTheme.typography.labelLarge)
            CategoryChips(categories = categories, selectedId = categoryId, onSelect = { categoryId = it })

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Date: " + CycleUtils.dayLabel(timestamp))
            }

            val amount = amountText.toLongOrNull()
            Button(
                onClick = {
                    if (amount != null && amount > 0 && categoryId != null) {
                        if (existing == null) {
                            vm.saveFullEntry(amount, categoryId!!, note, timestamp) { onBack() }
                        } else {
                            vm.updateEntry(existing, amount, categoryId!!, note, timestamp)
                            onBack()
                        }
                    }
                },
                enabled = (amount ?: 0) > 0 && categoryId != null,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (existing == null) "Save" else "Update") }
        }
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = timestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val picked = dpState.selectedDateMillis ?: timestamp
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = picked
                        set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0)
                    }
                    timestamp = cal.timeInMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = dpState) }
    }
}
