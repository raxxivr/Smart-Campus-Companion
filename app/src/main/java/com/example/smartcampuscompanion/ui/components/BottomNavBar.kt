package com.example.smartcampuscompanion.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.ui.theme.TealPrimary

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onCampusClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val items = listOf("Home", "Announcements", "Tasks", "Campus", "Settings")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Campaign,
        Icons.Default.Checklist,
        Icons.Default.School,
        Icons.Default.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = item,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    when (index) {
                        0 -> onHomeClick()
                        1 -> onAnnouncementsClick()
                        2 -> onTasksClick()
                        3 -> onCampusClick()
                        4 -> onSettingsClick()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = TealPrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
