package com.kharchabook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kharchabook.data.ExpenseRow
import com.kharchabook.ui.AppViewModel
import com.kharchabook.ui.screens.*
import com.kharchabook.ui.theme.KharchaBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KharchaBookTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val vm: AppViewModel = viewModel()
    
    // Navigation items
    val items = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Categories", "categories", Icons.Default.List),
        BottomNavItem("Settings", "settings", Icons.Default.Settings)
    )

    // Track current route for bottom nav
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Track if we need to edit an expense
    var entryToEdit by remember { mutableStateOf<ExpenseRow?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    vm = vm,
                    onOpenQuickAdd = {
                        entryToEdit = null
                        navController.navigate("entry")
                    },
                    onEditEntry = { row ->
                        entryToEdit = row
                        navController.navigate("entry")
                    }
                )
            }
            composable("entry") {
                EntryEditScreen(
                    vm = vm,
                    existing = entryToEdit,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("categories") {
                CategoriesScreen(vm)
            }
            composable("settings") {
                SettingsScreen(vm)
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)
