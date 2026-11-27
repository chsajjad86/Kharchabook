package com.kharchabook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kharchabook.data.Category
import com.kharchabook.ui.AppViewModel
import com.kharchabook.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(vm: AppViewModel) {
    val categories by vm.categories.collectAsState()
    var editing by remember { mutableStateOf<Category?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var blockedMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "Add") }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (categories.isEmpty()) item { EmptyState("No categories yet. Tap + to add.") }
            items(categories, key = { it.id }) { c ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(c.name, fontWeight = FontWeight.SemiBold)
                            if (c.keywords.isNotEmpty()) {
                                Text(
                                    c.keywords.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                        IconButton(onClick = { editing = c }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                        IconButton(onClick = {
                            vm.deleteCategory(c) { blockedMsg = "Cannot delete \"${c.name}\" — it has expenses." }
                        }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                    }
                }
            }
        }
    }

    if (showAdd || editing != null) {
        CategoryEditDialog(
            initial = editing,
            onDismiss = { showAdd = false; editing = null },
            onSave = { name, kws ->
                if (editing == null) vm.addCategory(name, kws)
                else vm.updateCategory(editing!!.copy(name = name, keywords = kws))
                showAdd = false; editing = null
            }
        )
    }

    if (blockedMsg != null) {
        AlertDialog(
            onDismissRequest = { blockedMsg = null },
            confirmButton = { TextButton(onClick = { blockedMsg = null }) { Text("OK") } },
            title = { Text("Cannot delete") },
            text = { Text(blockedMsg!!) }
        )
    }
}

@Composable
fun CategoryEditDialog(
    initial: Category?,
    onDismiss: () -> Unit,
    onSave: (String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var keywords by remember { mutableStateOf(initial?.keywords?.joinToString(", ") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New category" else "Edit category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("Keywords (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val kws = keywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (name.isNotBlank()) onSave(name.trim(), kws)
                },
                enabled = name.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
