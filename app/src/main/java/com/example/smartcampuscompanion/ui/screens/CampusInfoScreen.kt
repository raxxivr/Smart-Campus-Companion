package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.ui.theme.TealPrimary

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val office: String
)

val departmentList = listOf(
    Department("College of Computing Studies", "ccs@example.com", "123-456-7890", "Building A, Room 101"),
    Department("College of Education", "coed@example.com", "123-456-7891", "Building B, Room 202"),
    Department("College of Engineering", "coe@example.com", "123-456-7892", "Building C, Room 303"),
    Department("College of Health and Allied Sciences", "chas@example.com", "123-456-7893", "Building D, Room 404"),
    Department("College of Arts and Sciences", "cas@example.com", "123-456-7894", "Building E, Room 505"),
    Department("College of Business, Accountancy and Administration", "cbaa@example.com", "123-456-7895", "Building F, Room 606")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Campus Information", 
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
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFF))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(departmentList) { department ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = department.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Email", value = department.email)
                        InfoRow(label = "Phone", value = department.phone)
                        InfoRow(label = "Office", value = department.office)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.DarkGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CampusInfoScreenPreview() {
    SmartCampusCompanionTheme {
        CampusInfoScreen(onBackClick = {})
    }
}
