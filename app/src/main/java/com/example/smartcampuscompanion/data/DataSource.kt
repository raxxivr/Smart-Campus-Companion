package com.example.smartcampuscompanion.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val location: String, // Renamed from description to location for clarity
    val icon: ImageVector
)

object DataSource {
    val departments = listOf(
        Department("College of Computing Studies", "ccs@example.com", "123-456-7890", "Building A, Room 101", Icons.Default.Computer),
        Department("College of Education", "coed@example.com", "123-456-7891", "Building B, Room 202", Icons.Default.School),
        Department("College of Engineering", "coe@example.com", "123-456-7892", "Building C, Room 303", Icons.Default.Engineering),
        Department("College of Health and Allied Sciences", "chas@example.com", "123-456-7893", "Building D, Room 404", Icons.Default.LocalHospital),
        Department("College of Arts and Sciences", "cas@example.com", "123-456-7894", "Building E, Room 505", Icons.Default.AccountBalance),
        Department("College of Business, Accountancy and Administration", "cbaa@example.com", "123-456-7895", "Building F, Room 606", Icons.Default.Business)
    )
}
