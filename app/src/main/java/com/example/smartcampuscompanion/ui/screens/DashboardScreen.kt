package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.ui.components.BottomNavBar
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    username: String?,
    taskViewModel: TaskViewModel,
    onLogoutClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onCampusInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Smart Campus Companion",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TealPrimary,
                    navigationIconContentColor = TealPrimary
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedIndex = 0,
                onHomeClick = { /* Already here */ },
                onAnnouncementsClick = onAnnouncementsClick,
                onTasksClick = onTasksClick,
                onCampusClick = onCampusInfoClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        DashboardContent(
            username = username,
            taskViewModel = taskViewModel,
            onLogoutRequest = { showLogoutDialog = true },
            onCalendarClick = onCalendarClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DashboardContent(
    username: String?,
    taskViewModel: TaskViewModel,
    onLogoutRequest: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by taskViewModel.allTasks.collectAsState()
    
    val calendar = Calendar.getInstance()
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
    val dayNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val fullDate = "$dayName, $monthName $dayNumber"

    // Logic for finding current or next day tasks
    val todayTasks = tasks.filter { isSameDay(it.dueDate, calendar.timeInMillis) && !it.isCompleted }
    
    val tomorrow = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
    val tomorrowTasks = tasks.filter { isSameDay(it.dueDate, tomorrow.timeInMillis) && !it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFF)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello, ${username ?: "student"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: 2300999 • BSIT - 3rd Year",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                IconButton(
                    onClick = onLogoutRequest,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = TealPrimary)
                }
            }
        }

        // Calendar Widget Section
        item {
            CalendarWidget(
                dayNumber = dayNumber,
                dayName = dayName,
                tasksToShow = if (todayTasks.isNotEmpty()) todayTasks else tomorrowTasks,
                isTomorrow = todayTasks.isEmpty() && tomorrowTasks.isNotEmpty(),
                onClick = onCalendarClick
            )
        }

        // Featured Events Section
        item {
            Column {
                SectionHeader(title = "Featured Events")
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    val events = listOf(
                        Pair("Art Exhibition", R.drawable.art_exhibit_image),
                        Pair("Tech Summit", R.drawable.tech_summit_image),
                        Pair("Career Fair", R.drawable.career_fair_image)
                    )
                    items(events) { event ->
                        EventCard(title = event.first, imageRes = event.second)
                    }
                }
            }
        }

        // Recent Announcements Section
        item {
            Column {
                SectionHeader(title = "Recent Announcements")
                Spacer(modifier = Modifier.height(12.dp))
                AnnouncementItem(
                    title = "Library Hours Extension",
                    desc = "The main library will be open until midnight during finals week.",
                    time = "2h ago"
                )
                AnnouncementItem(
                    title = "New Canteen Menu",
                    desc = "Check out the healthy options available starting tomorrow at the student center.",
                    time = "5h ago"
                )
            }
        }
    }
}

@Composable
fun CalendarWidget(
    dayNumber: String,
    dayName: String,
    tasksToShow: List<Task>,
    isTomorrow: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Text(
                    text = dayName.take(3).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dayNumber,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Column(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (isTomorrow) {
                    Text(
                        "Tomorrow's Reminder:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                if (tasksToShow.isEmpty()) {
                    Text(
                        "No tasks today",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    tasksToShow.take(2).forEachIndexed { index, task ->
                        UpcomingItem(
                            title = task.title,
                            time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(task.dueDate)),
                            category = task.category
                        )
                        if (index == 0 && tasksToShow.size > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingItem(title: String, time: String, category: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(TealPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$time • $category",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = { /* View All */ }) {
            Text("View All", color = TealPrimary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun EventCard(title: String, imageRes: Int) {
    Card(
        modifier = Modifier.width(240.dp).height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Feb 28 • Main Ground",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun AnnouncementItem(title: String, desc: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TealPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = TealPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }
        }
    }
}

// Helper function to check if two timestamps are on the same day
private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
