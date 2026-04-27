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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.data.*
import com.example.smartcampuscompanion.data.repository.FirebaseUserRepository
import com.example.smartcampuscompanion.data.repository.SmartCampusAnnouncementRepository
import com.example.smartcampuscompanion.data.repository.SmartCampusTaskRepository
import com.example.smartcampuscompanion.ui.screens.*
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smartcampuscompanion.service.AnnouncementService
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcampuscompanion.data.repository.UserPreferencesRepository
import com.example.smartcampuscompanion.worker.WorkManagerHelper
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startAnnouncementService()
        }
    }

    private fun startAnnouncementService() {
        val intent = Intent(this, AnnouncementService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        splashScreen.setKeepOnScreenCondition { false }
        
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startAnnouncementService()
            }
        } else {
            startAnnouncementService()
        }

        WorkManagerHelper.scheduleAnnouncementTasks(this)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val credentialManager = remember { CredentialManager.create(context) }
            
            // Firebase Instances
            val auth = remember { FirebaseAuth.getInstance() }
            val firestore = remember { FirebaseFirestore.getInstance() }
            
            // Local Database
            val taskDatabase = remember { TaskDatabase.getDatabase(context) }
            
            // Repositories
            val sessionManager = remember { SessionManager(context) }
            val userRepository = remember { 
                FirebaseUserRepository(auth, firestore, taskDatabase.userDao()) 
            }
            val taskRepository = remember { 
                SmartCampusTaskRepository(firestore, taskDatabase.taskDao()) 
            }
            val announcementRepository = remember { 
                SmartCampusAnnouncementRepository(firestore, taskDatabase.announcementDao()) 
            }
            val userPrefsRepository = remember { UserPreferencesRepository(context) }

            // ViewModels
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(sessionManager, userRepository)
            )
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(taskRepository)
            )
            val announcementViewModel: AnnouncementViewModel = viewModel(
                factory = AnnouncementViewModelFactory(announcementRepository)
            )
            val campusInfoViewModel: CampusInfoViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(userPrefsRepository)
            )
            val signupViewModel: SignupViewModel = viewModel(
                factory = SignupViewModelFactory(userRepository)
            )

            val navController = rememberNavController()
            val isLoggedIn by loginViewModel.isLoggedIn
            var showSplash by remember { mutableStateOf(true) }
            val darkMode by settingsViewModel.darkModeEnabled.collectAsStateWithLifecycle()

            LaunchedEffect(isLoggedIn, showSplash) {
                if (!showSplash) {
                    if (isLoggedIn) {
                        loginViewModel.userEmail?.let { email ->
                            taskViewModel.loadTasksForUser(email)
                            announcementViewModel.loadReadStatus(email)
                        }
                        
                        val role = sessionManager.getRole()
                        val targetRoute = if (intent?.getBooleanExtra("OPEN_ANNOUNCEMENTS", false) == true) {
                            "announcements"
                        } else if (role == "ADMIN") {
                            "admin_dashboard"
                        } else {
                            "dashboard"
                        }

                        navController.navigate(targetRoute) {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                sessionManager.getEmail()?.let { email ->
                    taskViewModel.loadTasksForUser(email)
                    announcementViewModel.loadReadStatus(email)
                }
            }

            SmartCampusCompanionTheme(darkTheme = darkMode) {
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            NavHost(
                                navController = navController,
                                startDestination = if (sessionManager.isLoggedIn()) {
                                    if (sessionManager.getRole() == "ADMIN") "admin_dashboard" else "dashboard"
                                } else "login",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("login") {
                                    LoginScreen(
                                        viewModel = loginViewModel,
                                        onLoginClick = { email, password ->
                                            loginViewModel.login(email, password)
                                        },
                                        onSignUpClick = {
                                            signupViewModel.clearForm()
                                            navController.navigate("signup")
                                        },
                                        onGoogleSignInClick = {
                                            scope.launch {
                                                loginViewModel.setLoading(true)
                                                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                                                    .setFilterByAuthorizedAccounts(false)
                                                    .setServerClientId("1019161334414-dfkotnvcne1a7e1jmqi88ubm6dkljqt5.apps.googleusercontent.com")
                                                    .setAutoSelectEnabled(true)
                                                    .build()

                                                val request = GetCredentialRequest.Builder()
                                                    .addCredentialOption(googleIdOption)
                                                    .build()

                                                try {
                                                    val result = credentialManager.getCredential(context, request)
                                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                                    loginViewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                                } catch (e: Exception) {
                                                    loginViewModel.setLoading(false)
                                                    Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
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
                                }

                                composable("dashboard") {
                                    DashboardScreen(
                                        fullName = loginViewModel.fullName,
                                        studentNumber = loginViewModel.studentNumber,
                                        course = loginViewModel.course,
                                        taskViewModel = taskViewModel,
                                        announcementViewModel = announcementViewModel,
                                        campusViewModel = campusInfoViewModel,
                                        onAnnouncementsClick = { navController.navigate("announcements") },
                                        onTasksClick = { navController.navigate("task_manager") },
                                        onCampusInfoClick = { navController.navigate("campus_info") },
                                        onSettingsClick = { navController.navigate("settings") },
                                        onCalendarClick = { navController.navigate("calendar_module") }
                                    )
                                }

                                composable("admin_dashboard") {
                                    AdminDashboardScreen(
                                        loginViewModel = loginViewModel,
                                        announcementViewModel = announcementViewModel,
                                        onLogoutClick = { loginViewModel.logout() }
                                    )
                                }

                                composable("announcements") {
                                    val isAdmin = sessionManager.getRole() == "ADMIN"
                                    AnnouncementScreen(
                                        isAdmin = isAdmin,
                                        viewModel = announcementViewModel,
                                        onBackClick = { navController.popBackStack() },
                                        onHomeClick = { 
                                            val dest = if (isAdmin) "admin_dashboard" else "dashboard"
                                            navController.navigate(dest) 
                                        },
                                        onTasksClick = { navController.navigate("task_manager") },
                                        onCampusClick = { navController.navigate("campus_info") },
                                        onSettingsClick = { navController.navigate("settings") }
                                    )
                                }

                                composable("campus_info") {
                                    if (sessionManager.getRole() == "ADMIN") {
                                        LaunchedEffect(Unit) { navController.navigate("admin_dashboard") }
                                    } else {
                                        CampusInfoScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onHomeClick = { navController.navigate("dashboard") },
                                            onAnnouncementsClick = { navController.navigate("announcements") },
                                            onTasksClick = { navController.navigate("task_manager") },
                                            onSettingsClick = { navController.navigate("settings") },
                                            viewModel = campusInfoViewModel
                                        )
                                    }
                                }

                                composable("task_manager") {
                                    if (sessionManager.getRole() == "ADMIN") {
                                        LaunchedEffect(Unit) { navController.navigate("admin_dashboard") }
                                    } else {
                                        TaskManagerScreen(
                                            viewModel = taskViewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onHomeClick = { navController.navigate("dashboard") },
                                            onAnnouncementsClick = { navController.navigate("announcements") },
                                            onTasksClick = { /* Already here */ },
                                            onCampusClick = { navController.navigate("campus_info") },
                                            onSettingsClick = { navController.navigate("settings") }
                                        )
                                    }
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
                                        studentNumber = loginViewModel.studentNumber,
                                        course = loginViewModel.course,
                                        onLogout = { loginViewModel.logout() },
                                        viewModel = settingsViewModel,
                                        onHomeClick = { 
                                            val dest = if (sessionManager.getRole() == "ADMIN") "admin_dashboard" else "dashboard"
                                            navController.navigate(dest) 
                                        },
                                        onAnnouncementsClick = { navController.navigate("announcements") },
                                        onTasksClick = { navController.navigate("task_manager") },
                                        onCampusClick = { navController.navigate("campus_info") },
                                        onEditProfileClick = { navController.navigate("edit_profile") },
                                        onPrivacyClick = { navController.navigate("privacy") }
                                    )
                                }

                                composable("edit_profile") {
                                    EditProfileScreen(
                                        viewModel = loginViewModel,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }

                                composable("privacy") {
                                    PrivacyScreen(
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                            }

                            val isLoading by loginViewModel.isLoading
                            if (isLoading) {
                                LoadingScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
