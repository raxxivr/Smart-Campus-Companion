package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.ui.theme.TealPrimary

data class Announcement(
    val title: String,
    val description: String,
    val date: String
)


val announcementList = listOf(
    Announcement("Library Extended Hours", "Library will be open until 12AM during finals week.", "Feb 20, 2026"),
    Announcement("New Canteen Menu", "Healthy meals added starting this week.", "Feb 18, 2026"),
    Announcement("Enrollment Schedule", "2nd semester enrollment starts March 1.", "Feb 15, 2026")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(onBackClick: () -> Unit) {

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
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(announcementList) { announcement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { /* TODO: Handle announcement click */ },
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(announcement.title, fontWeight = FontWeight.Bold)
                            Text(announcement.description)
                            Text(
                                announcement.date,
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