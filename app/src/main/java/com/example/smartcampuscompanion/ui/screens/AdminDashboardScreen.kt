package com.example.smartcampuscompanion.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.ui.theme.TealSecondary
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanion.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    loginViewModel: LoginViewModel,
    announcementViewModel: AnnouncementViewModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var announcementToEdit by remember { mutableStateOf<Announcement?>(null) }
    var announcementToDelete by remember { mutableStateOf<Announcement?>(null) }
    
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Admin Portal",
                        fontWeight = FontWeight.ExtraBold,
                        color = TealPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    announcementToEdit = null
                    showDialog = true 
                },
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Announcement")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFB))
        ) {
            // Admin Stats Header
            AdminStatsHeader(loginViewModel.fullName ?: "Admin")

            Text(
                text = "Manage Announcements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Dedicated List for CRUD
            AnnouncementManagementList(
                viewModel = announcementViewModel,
                onEdit = { announcement ->
                    announcementToEdit = announcement
                    showDialog = true
                },
                onDelete = { announcement ->
                    announcementToDelete = announcement
                }
            )
        }
    }

    if (showDialog) {
        AdminAnnouncementDialog(
            announcement = announcementToEdit,
            onDismiss = { 
                showDialog = false
                announcementToEdit = null
            },
            onConfirm = { title, desc ->
                if (announcementToEdit == null) {
                    announcementViewModel.postAnnouncement(title, desc) { success ->
                        if (success) Toast.makeText(context, "Announcement Posted!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "Failed to post announcement", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val updated = announcementToEdit!!.copy(title = title, description = desc)
                    announcementViewModel.updateAnnouncement(updated) { success ->
                        if (success) Toast.makeText(context, "Announcement Updated!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "Failed to update announcement", Toast.LENGTH_SHORT).show()
                    }
                }
                showDialog = false
                announcementToEdit = null
            }
        )
    }

    if (announcementToDelete != null) {
        AlertDialog(
            onDismissRequest = { announcementToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this announcement? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        announcementToDelete?.let { announcement ->
                            announcementViewModel.deleteAnnouncement(announcement) { success ->
                                if (success) Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                        announcementToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { announcementToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminStatsHeader(adminName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(TealPrimary, TealSecondary)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Welcome back,",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = adminName,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "System Administrator",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun AnnouncementManagementList(
    viewModel: AnnouncementViewModel,
    onEdit: (Announcement) -> Unit,
    onDelete: (Announcement) -> Unit
) {
    val announcements by viewModel.allAnnouncements.collectAsState()

    if (announcements.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No announcements to manage", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(announcements) { announcement ->
                AdminAnnouncementCard(
                    announcement = announcement,
                    onDelete = { onDelete(announcement) },
                    onEdit = { onEdit(announcement) }
                )
            }
        }
    }
}

@Composable
fun AdminAnnouncementCard(announcement: Announcement, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = announcement.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun AdminAnnouncementDialog(
    announcement: Announcement?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(announcement?.title ?: "") }
    var desc by remember { mutableStateOf(announcement?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (announcement == null) "Post New Announcement" else "Edit Announcement") },
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
            ) { Text(if (announcement == null) "Post" else "Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
