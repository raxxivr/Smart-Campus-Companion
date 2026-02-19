package com.example.smartcampuscompanion.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.ui.screens.AccountScreen
import com.example.smartcampuscompanion.ui.screens.CampusInfoScreen
import com.example.smartcampuscompanion.ui.screens.DashboardScreen

@Composable
fun MainApp(
    username: String?,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                username = username,
                onLogoutClick = onLogoutClick,
                onCampusInfoClick = { navController.navigate("campus_info") },
                onAccountClick = { navController.navigate("account") }
            )
        }
        composable("campus_info") {
            CampusInfoScreen(onBackClick = { navController.popBackStack() })
        }
        composable("account") {
            AccountScreen(
                username = username,
                onLogoutClick = onLogoutClick,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
