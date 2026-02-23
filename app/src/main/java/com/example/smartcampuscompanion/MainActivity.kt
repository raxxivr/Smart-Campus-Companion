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
import com.example.smartcampuscompanion.data.*
import com.example.smartcampuscompanion.ui.screens.*
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            
            val sessionManager = remember { SessionManager(context) }
            val taskDatabase = remember { TaskDatabase.getDatabase(context) }
            val taskRepository = remember { TaskRepository(taskDatabase.taskDao()) }

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(sessionManager)
            )
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(taskRepository)
            )

            val navController = rememberNavController()
            val isLoggedIn by loginViewModel.isLoggedIn
            val loginError by loginViewModel.loginError
            val isLoading by loginViewModel.isLoading

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
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            LaunchedEffect(loginError) {
                loginError?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    loginViewModel.clearError()
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
                                        loginViewModel.login(username, password)
                                    }
                                )
                            }

                            composable("dashboard") {
                                DashboardScreen(
                                    username = loginViewModel.username,
                                    onLogoutClick = { loginViewModel.logout() },
                                    onAnnouncementsClick = { /* navController.navigate("announcements") */ },
                                    onTasksClick = { navController.navigate("task_manager") },
                                    onCampusInfoClick = { navController.navigate("campus_info") },
                                    onSettingsClick = { /* navController.navigate("settings") */ }
                                )
                            }

                            composable("campus_info") {
                                CampusInfoScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("task_manager") {
                                TaskManagerScreen(
                                    viewModel = taskViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onHomeClick = { navController.navigate("dashboard") },
                                    onAnnouncementsClick = { /* navController.navigate("announcements") */ },
                                    onCampusClick = { navController.navigate("campus_info") },
                                    onSettingsClick = { /* navController.navigate("settings") */ }
                                )
                            }
                        }

                        if (isLoading) {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }
}
