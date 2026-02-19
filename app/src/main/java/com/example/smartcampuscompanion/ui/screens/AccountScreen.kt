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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    username: String?,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "My Account", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TealPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFF))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Profile Header
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(TealPrimary.copy(alpha = 0.1f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(60.dp), tint = TealPrimary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = username ?: "Student User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "student.id@university.edu", // Static for now
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Settings Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "General Settings",
                        style = MaterialTheme.typography.labelLarge,
                        color = TealPrimary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    SettingsItemGroup {
                        SettingsItem(icon = Icons.Default.AccountCircle, title = "Edit Profile")
                        SettingsItem(icon = Icons.Default.Notifications, title = "Notifications")
                        SettingsItem(icon = Icons.Default.Lock, title = "Privacy & Security")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    SettingsItemGroup {
                        SettingsItem(icon = Icons.Default.Info, title = "About")
                        SettingsItem(icon = Icons.Default.Build, title = "App Version", value = "1.0.0")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Logout Button
            item {
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE5E5))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsItemGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle Click */ }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        } else {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp, modifier = Modifier.padding(start = 60.dp))
}


@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    SmartCampusCompanionTheme {
        AccountScreen(username = "John Doe", onLogoutClick = {}, onBackClick = {})
    }
}
