package com.kharchabook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kharchabook.data.Category
import com.kharchabook.data.ExpenseRow
import com.kharchabook.ui.AppViewModel
import com.kharchabook.ui.components.EmptyState
import com.kharchabook.ui.components.TotalCard
import com.kharchabook.ui.pkr
import com.kharchabook.util.CycleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: AppViewModel,
    onOpenQuickAdd: () -> Unit,
    onEditEntry: (ExpenseRow) -> Unit
) {
    val todayTotal by vm.todayTotal.collectAsState()
    val cycleTotal by vm.cycleTotal.collectAsState()
    val cycleLabel by vm.cycleLabel.collectAsState()
    val rows by vm.cycleRows.collectAsState()
    val catTotals by vm.categoryTotals.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kharcha Book", fontWeight = FontWeight.SemiBold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TotalCard(
                    title = "Today",
                    amount = todayTotal,
                    subtitle = CycleUtils.shortDayLabel(System.currentTimeMillis())
                )
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { vm.prevCycle() }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev")
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Salary Cycle", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(cycleLabel, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { vm.nextCycle() }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(pkr(cycleTotal), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        if (cycleLabel.isNotEmpty()) {
                            TextButton(onClick = { vm.resetToCurrentCycle() }) { Text("Back to current cycle") }
                        }
                    }
                }
            }

            if (catTotals.isNotEmpty()) {
                item {
                    Text("By category (this cycle)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(catTotals) { ct ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ct.categoryName)
                        Text(pkr(ct.total), fontWeight = FontWeight.Medium)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Entries in cycle", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (rows.isEmpty()) {
                item { EmptyState("No expenses in this cycle yet. Tap + to add.") }
            } else {
                items(rows, key = { it.id }) { row ->
                    EntryRow(row = row, onClick = { onEditEntry(row) }, onDelete = { vm.deleteEntry(row) })
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showSheet) {
        QuickAddSheet(
            vm = vm,
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            onOpenFullForm = {
                showSheet = false
                onOpenQuickAdd()
            }
        )
    }
}

@Composable
fun EntryRow(row: ExpenseRow, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(row.categoryName, fontWeight = FontWeight.SemiBold)
                if (row.note.isNotBlank()) {
                    Text(row.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    CycleUtils.dayLabel(row.timestamp) + " · " + CycleUtils.timeLabel(row.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(pkr(row.amount), fontWeight = FontWeight.SemiBold)
            TextButton(onClick = onDelete) { Text("Del") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickAddSheet(
    vm: AppViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOpenFullForm: () -> Unit
) {
    val categories by vm.categories.collectAsState()
    var input by remember { mutableStateOf("") }
    var overrideId by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val draft = remember(input, categories) { vm.previewDraft(input) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Quick add", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it; error = null },
                label = { Text("e.g. 200 bread") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = { if (error != null) Text(error!!) }
            )

            Text("Category", style = MaterialTheme.typography.labelLarge)
            CategoryChips(
                categories = categories,
                selectedId = overrideId ?: draft.suggestedCategory?.id,
                onSelect = { overrideId = it }
            )
            if (draft.amount != null) {
                Text("Amount detected: ${pkr(draft.amount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        vm.saveQuickEntry(input, overrideId, onError = { error = it }, onDone = {
                            input = ""; overrideId = null; onDismiss()
                        })
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
                OutlinedButton(onClick = onOpenFullForm, modifier = Modifier.weight(1f)) { Text("More options") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChips(categories: List<Category>, selectedId: Long?, onSelect: (Long) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { c ->
            FilterChip(
                selected = c.id == selectedId,
                onClick = { onSelect(c.id) },
                label = { Text(c.name) }
            )
        }
    }
}
