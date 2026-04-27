package com.example.smartcampuscompanion.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.smartcampuscompanion.domain.model.Announcement
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
    
    var showPostDialog by remember { mutableStateOf(false) }
    var announcementToEdit by remember { mutableStateOf<Announcement?>(null) }
    var announcementToDelete by remember { mutableStateOf<Announcement?>(null) }
    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Announcements", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = TealPrimary
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
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
                    onClick = { 
                        announcementToEdit = null
                        showPostDialog = true 
                    },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (announcements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No announcements yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            onClick = {
                                selectedAnnouncement = announcement
                                if (!isAdmin && !isRead) {
                                    viewModel.markAsRead(announcement.id)
                                }
                            },
                            onEdit = {
                                announcementToEdit = announcement
                                showPostDialog = true
                            },
                            onDelete = {
                                announcementToDelete = announcement
                            }
                        )
                    }
                }
            }
        }

        if (showPostDialog) {
            PostAnnouncementDialog(
                announcement = announcementToEdit,
                onDismiss = { 
                    showPostDialog = false
                    announcementToEdit = null
                },
                onConfirm = { title, desc ->
                    if (announcementToEdit == null) {
                        viewModel.postAnnouncement(title, desc) { success ->
                            if (success) Toast.makeText(context, "Announcement Posted!", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(context, "Failed to post announcement", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val updated = announcementToEdit!!.copy(title = title, description = desc)
                        viewModel.updateAnnouncement(updated) { success ->
                            if (success) Toast.makeText(context, "Announcement Updated!", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(context, "Failed to update announcement", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showPostDialog = false
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
                                viewModel.deleteAnnouncement(announcement) { success ->
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

        selectedAnnouncement?.let { announcement ->
            AnnouncementDetailDialog(
                announcement = announcement,
                onDismiss = { selectedAnnouncement = null }
            )
        }
    }
}

@Composable
fun AnnouncementItemCard(
    announcement: Announcement,
    isAdmin: Boolean,
    isRead: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val effectiveIsRead = if (isAdmin) false else isRead
    val backgroundColor = if (effectiveIsRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    val contentAlpha = if (effectiveIsRead) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(if (effectiveIsRead) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (effectiveIsRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else TealPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Campaign,
                    contentDescription = null,
                    tint = if (effectiveIsRead) MaterialTheme.colorScheme.onSurfaceVariant else TealPrimary,
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
                        color = if (effectiveIsRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = announcement.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Read more",
                        style = MaterialTheme.typography.labelMedium,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (isAdmin) {
                Row {
                    IconButton(onClick = { 
                        onEdit() 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary.copy(alpha = 0.8f))
                    }
                    IconButton(onClick = { onDelete() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
fun PostAnnouncementDialog(
    announcement: Announcement? = null,
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
            ) { Text(if (announcement == null) "Post" else "Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AnnouncementDetailDialog(
    announcement: Announcement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    // Header Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = "Announcement Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            modifier = Modifier.align(Alignment.CenterStart).padding(top = 16.dp, end = 40.dp)
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Posted on ${announcement.date}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = announcement.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}
