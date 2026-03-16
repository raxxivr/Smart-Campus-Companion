package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.ui.components.BottomNavBar
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    isAdmin: Boolean,
    viewModel: AnnouncementViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTasksClick: () -> Unit,
    onCampusClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val announcements by viewModel.allAnnouncements.collectAsState()
    val readAnnouncementIds by viewModel.readAnnouncementIds.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Announcements", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = TealPrimary
                    )
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            }
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
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Post Announcement")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBFBFF))
        ) {
            if (announcements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No announcements yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(announcements) { announcement ->
                        val isRead = readAnnouncementIds.contains(announcement.id)
                        AnnouncementItemCard(
                            announcement = announcement,
                            isAdmin = isAdmin,
                            isRead = isRead,
                            onMarkAsRead = { if (!isAdmin) viewModel.markAsRead(announcement.id) },
                            onDelete = { viewModel.deleteAnnouncement(announcement) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            PostAnnouncementDialog(
                onDismiss = { showDialog = false },
                onConfirm = { title, desc ->
                    viewModel.postAnnouncement(title, desc)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AnnouncementItemCard(
    announcement: Announcement,
    isAdmin: Boolean,
    isRead: Boolean,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val effectiveIsRead = if (isAdmin) false else isRead
    val backgroundColor = if (effectiveIsRead) Color(0xFFF5F5F5) else Color.White
    val contentAlpha = if (effectiveIsRead) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !effectiveIsRead && !isAdmin) { onMarkAsRead() },
        elevation = CardDefaults.cardElevation(if (effectiveIsRead) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (effectiveIsRead) Color.LightGray.copy(alpha = 0.3f) else TealPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Campaign,
                    contentDescription = null,
                    tint = if (effectiveIsRead) Color.Gray else TealPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = announcement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (effectiveIsRead) Color.Gray else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = announcement.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = contentAlpha)
                    )
                }
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray.copy(alpha = contentAlpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun PostAnnouncementDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Post New Announcement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc) },
                enabled = title.isNotBlank() && desc.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) { Text("Post") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
