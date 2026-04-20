package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.data.Department
import com.example.smartcampuscompanion.domain.model.Task
import com.example.smartcampuscompanion.ui.components.BottomNavBar
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanion.viewmodel.CampusInfoViewModel
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    fullName: String?,
    studentNumber: String?,
    course: String?,
    taskViewModel: TaskViewModel,
    announcementViewModel: AnnouncementViewModel,
    campusViewModel: CampusInfoViewModel,
    onAnnouncementsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onCampusInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
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
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            }
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
            fullName = fullName,
            studentNumber = studentNumber,
            course = course,
            taskViewModel = taskViewModel,
            announcementViewModel = announcementViewModel,
            campusViewModel = campusViewModel,
            onAnnouncementsClick = onAnnouncementsClick,
            onCampusInfoClick = onCampusInfoClick,
            onCalendarClick = onCalendarClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DashboardContent(
    fullName: String?,
    studentNumber: String?,
    course: String?,
    taskViewModel: TaskViewModel,
    announcementViewModel: AnnouncementViewModel,
    campusViewModel: CampusInfoViewModel,
    onAnnouncementsClick: () -> Unit,
    onCampusInfoClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by taskViewModel.allTasks.collectAsState()
    val announcements by announcementViewModel.allAnnouncements.collectAsState()
    val departments by campusViewModel.departments.collectAsState()
    val readAnnouncementIds by announcementViewModel.readAnnouncementIds.collectAsState()
    
    val calendar = Calendar.getInstance()
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    val dayNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()

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
            Column {
                Text(
                    text = "Hello, ${fullName ?: "student"}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${studentNumber ?: "2024-XXXX"} • ${course ?: "Regular Student"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
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

        // Campus Info Preview Section
        item {
            Column {
                SectionHeader(
                    title = "Campus Info Overview",
                    onViewAllClick = onCampusInfoClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                if (departments.isEmpty()) {
                    Text("No campus info available.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                } else {
                    departments.take(3).forEach { department ->
                        CampusInfoPreviewItem(department = department, onClick = onCampusInfoClick)
                    }
                }
            }
        }

        // Recent Announcements Section
        item {
            Column {
                SectionHeader(
                    title = "Recent Announcements",
                    onViewAllClick = onAnnouncementsClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                val unreadAnnouncements = announcements.filter { !readAnnouncementIds.contains(it.id) }

                if (unreadAnnouncements.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No new announcements available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    unreadAnnouncements.take(3).forEach { announcement ->
                        AnnouncementItem(
                            announcement = announcement, 
                            isRead = false,
                            onMarkAsRead = { /* Dashboard items are not clickable for marking read as per requirement */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CampusInfoPreviewItem(department: Department, onClick: () -> Unit) {
    val isOpen = department.isOpen()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = department.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = department.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Surface(
                color = if (isOpen) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isOpen) "OPEN" else "CLOSED",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOpen) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
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
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TealPrimary.copy(alpha = 0.1f))
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = dayNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealPrimary
                )
                Text(
                    text = dayName.take(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTomorrow) "Upcoming for Tomorrow" else "Your Schedule for Today",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (tasksToShow.isEmpty()) {
                    Text(
                        text = "You're all caught up! No tasks due.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "${tasksToShow.size} tasks: ${tasksToShow.first().title}${if (tasksToShow.size > 1) "..." else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "View All",
            style = MaterialTheme.typography.labelLarge,
            color = TealPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement, isRead: Boolean, onMarkAsRead: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TealPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = TealPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
