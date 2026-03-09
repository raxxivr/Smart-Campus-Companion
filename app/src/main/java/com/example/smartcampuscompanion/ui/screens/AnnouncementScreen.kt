package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.ui.components.BottomNavBar
import com.example.smartcampuscompanion.ui.theme.TealPrimary

data class Announcement(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val isRead: Boolean = false // Use val for immutability
)


val initialAnnouncementList = listOf(
    Announcement(1, "Library Extended Hours", "Library will be open until 12AM during finals week.", "Feb 20, 2026"),
    Announcement(2, "New Canteen Menu", "Healthy meals added starting this week.", "Feb 18, 2026"),
    Announcement(3, "Enrollment Schedule", "2nd semester enrollment starts March 1.", "Feb 15, 2026")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTasksClick: () -> Unit,
    onCampusClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    var announcements by remember { mutableStateOf(initialAnnouncementList) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedIndex = 1,
                onHomeClick = onHomeClick,
                onAnnouncementsClick = { /* Already here */ },
                onTasksClick = onTasksClick,
                onCampusClick = onCampusClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(announcements, key = { it.id }) { announcement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            // Cleaner state update logic
                            announcements = announcements.map {
                                if (it.id == announcement.id) it.copy(isRead = true) else it
                            }
                        },
                    elevation = CardDefaults.cardElevation(if (announcement.isRead) 1.dp else 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (announcement.isRead) Color(0xFFF5F5F5) else Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                             Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (announcement.isRead) Color.Gray else TealPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            // Unread indicator dot
                            if (!announcement.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = announcement.title,
                                fontWeight = if (announcement.isRead) FontWeight.Normal else FontWeight.Bold,
                                color = if (announcement.isRead) Color.Gray else Color.Black
                            )
                            Text(
                                text = announcement.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (announcement.isRead) Color.Gray else Color.DarkGray
                            )
                            Text(
                                text = announcement.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
