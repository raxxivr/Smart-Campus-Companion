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
            val userRepository = remember { UserRepository(taskDatabase.userDao()) }
            val announcementRepository = remember { 
                AnnouncementRepository(
                    taskDatabase.announcementDao(),
                    taskDatabase.readAnnouncementDao()
                ) 
            }

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(sessionManager, userRepository)
            )
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(taskRepository)
            )
            val announcementViewModel: AnnouncementViewModel = viewModel(
                factory = AnnouncementViewModelFactory(announcementRepository)
            )
            val settingsViewModel: SettingsViewModel = viewModel()
            val signupViewModel: SignupViewModel = viewModel(
                factory = SignupViewModelFactory(userRepository)
            )

            val navController = rememberNavController()
            val isLoggedIn by loginViewModel.isLoggedIn
            val loginError by loginViewModel.loginError
            val isLoading by loginViewModel.isLoading

            val startDestination = remember {
                if (sessionManager.isLoggedIn()) "dashboard" else "login"
            }

            // Initialize data for the logged-in user on app launch
            LaunchedEffect(Unit) {
                sessionManager.getEmail()?.let { email ->
                    taskViewModel.loadTasksForUser(email)
                    announcementViewModel.loadReadStatus(email)
                }
            }

            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    loginViewModel.userEmail?.let { email ->
                        taskViewModel.loadTasksForUser(email)
                        announcementViewModel.loadReadStatus(email)
                    }
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
                                    onLoginClick = { email, password ->
                                        // loginViewModel handles both Admin hardcoded check and DB check
                                        loginViewModel.login(email, password)
                                    },
                                    onSignUpClick = {
                                        signupViewModel.clearForm()
                                        navController.navigate("signup")
                                    }
                                )
                            }

                            composable("signup") {
                                SignupScreen(
                                    onSignupClick = { _, _, _, _, _ ->
                                        signupViewModel.signup()
                                    },
                                    onBackToLoginClick = {
                                        navController.popBackStack()
                                    },
                                    viewModel = signupViewModel
                                )
                                
                                val uiState by signupViewModel.uiState.collectAsState()
                                if (uiState.isSignupSuccessful) {
                                    LaunchedEffect(Unit) {
                                        Toast.makeText(context, "Signup Successful! Please Login.", Toast.LENGTH_SHORT).show()
                                        signupViewModel.resetSignupSuccess()
                                        navController.popBackStack()
                                    }
                                }
                            }

                            composable("dashboard") {
                                DashboardScreen(
                                    fullName = loginViewModel.fullName,
                                    studentNumber = loginViewModel.studentNumber,
                                    course = loginViewModel.course,
                                    taskViewModel = taskViewModel,
                                    announcementViewModel = announcementViewModel,
                                    onAnnouncementsClick = { navController.navigate("announcements") },
                                    onTasksClick = { navController.navigate("task_manager") },
                                    onCampusInfoClick = { navController.navigate("campus_info") },
                                    onSettingsClick = { navController.navigate("settings") },
                                    onCalendarClick = { navController.navigate("calendar_module") }
                                )
                            }

                            composable("announcements") {
                                val isAdmin = loginViewModel.userEmail == "admin@smartcampus.com"
                                AnnouncementScreen(
                                    isAdmin = isAdmin,
                                    viewModel = announcementViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onHomeClick = { navController.navigate("dashboard") },
                                    onTasksClick = { navController.navigate("task_manager") },
                                    onCampusClick = { navController.navigate("campus_info") },
                                    onSettingsClick = { navController.navigate("settings") }
                                )
                            }

                            composable("campus_info") {
                                CampusInfoScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onHomeClick = { navController.navigate("dashboard") },
                                    onAnnouncementsClick = { navController.navigate("announcements") },
                                    onTasksClick = { navController.navigate("task_manager") },
                                    onSettingsClick = { navController.navigate("settings") }
                                )
                            }

                            composable("task_manager") {
                                TaskManagerScreen(
                                    viewModel = taskViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onHomeClick = { navController.navigate("dashboard") },
                                    onAnnouncementsClick = { navController.navigate("announcements") },
                                    onCampusClick = { navController.navigate("campus_info") },
                                    onSettingsClick = { navController.navigate("settings") }
                                )
                            }

                            composable("calendar_module") {
                                CalendarModuleScreen(
                                    taskViewModel = taskViewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    username = loginViewModel.fullName ?: loginViewModel.userEmail ?: "student",
                                    onLogout = { loginViewModel.logout() },
                                    viewModel = settingsViewModel,
                                    onHomeClick = { navController.navigate("dashboard") },
                                    onAnnouncementsClick = { navController.navigate("announcements") },
                                    onTasksClick = { navController.navigate("task_manager") },
                                    onCampusClick = { navController.navigate("campus_info") }
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
