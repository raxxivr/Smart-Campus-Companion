package com.example.smartcampuscompanion

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.data.SessionManager
import com.example.smartcampuscompanion.ui.screens.CampusInfoScreen
import com.example.smartcampuscompanion.ui.screens.DashboardScreen
import com.example.smartcampuscompanion.ui.screens.LoadingScreen
import com.example.smartcampuscompanion.ui.screens.LoginScreen
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.LoginViewModel
import com.example.smartcampuscompanion.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(sessionManager)
            )
            
            val navController = rememberNavController()
            val isLoggedIn by viewModel.isLoggedIn
            val loginError by viewModel.loginError
            val isLoading by viewModel.isLoading

            val startDestination = remember {
                if (sessionManager.isLoggedIn()) "dashboard" else "login"
            }

            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                        popUpTo("campus_info") { inclusive = true }
                    }
                }
            }

            LaunchedEffect(loginError) {
                loginError?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }

            SmartCampusCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("login") {
                                LoginScreen(
                                    onLoginClick = { username, password ->
                                        viewModel.login(username, password)
                                    }
                                )
                            }
                            composable("dashboard") {
                                val usernameState = remember { mutableStateOf(viewModel.username) }
                                DashboardScreen(
                                    username = usernameState,
                                    onLogoutClick = { viewModel.logout() },
                                    onCampusInfoClick = { navController.navigate("campus_info") }
                                )
                            }
                            composable("campus_info") {
                                CampusInfoScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                        
                        // Overlay Loading Screen when isLoading is true
                        if (isLoading) {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }
}
