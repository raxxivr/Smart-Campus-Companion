package com.example.smartcampuscompanion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Welcome to Smart Campus",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardButton(text = "Campus Information", icon = Icons.Default.Info) { /* Navigate */ }
            DashboardButton(text = "Academic Tasks", icon = Icons.Default.DateRange) { /* Navigate */ }
            DashboardButton(text = "Announcements", icon = Icons.Default.Notifications) { /* Navigate */ }
            DashboardButton(text = "Settings", icon = Icons.Default.Settings) { /* Navigate */ }
        }
    }
}

@Composable
fun DashboardButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
