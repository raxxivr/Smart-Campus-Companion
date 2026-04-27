package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & Privacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = TealPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrivacySection(
                title = "Data Collection",
                content = "We collect basic information such as your name, email, student number, and course to provide a personalized experience and manage your academic tasks effectively."
            )
            
            PrivacySection(
                title = "Information Usage",
                content = "Your data is used solely within the Smart Campus Companion ecosystem. We do not sell or share your personal information with third-party advertisers."
            )

            PrivacySection(
                title = "Cloud Security",
                content = "We use Google Firebase to store your data securely. All communications between the app and the cloud are encrypted using industry-standard SSL/TLS protocols."
            )

            PrivacySection(
                title = "User Rights",
                content = "You have the right to edit your profile at any time through the 'Edit Profile' section. Your password (if registered via email) is hashed and never stored in plain text."
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Last updated: April 2026",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun PrivacySection(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
