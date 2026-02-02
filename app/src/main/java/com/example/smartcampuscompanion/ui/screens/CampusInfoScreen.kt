package com.example.smartcampuscompanion.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val office: String
)

val departmentList = listOf(
    Department("College of Coputing Studies", "ccs@example.com", "123-456-7890", "Building A, Room 101"),
    Department("College of Education", "coed@example.com", "123-456-7891", "Building B, Room 202"),
    Department("College of Engineering", "coe@example.com", "123-456-7892", "Building C, Room 303"),
    Department("College of Health and Allied Sciences", "chas@example.com", "123-456-7893", "Building D, Room 404"),
    Department("College of Arts and Sciences", "cas@example.com", "123-456-7894", "Building E, Room 505"),
    Department("College of Business, Accountancy and Administration", "cbaa@example.com", "123-456-7895", "Building F, Room 606")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(modifier: Modifier = Modifier) {
    val greenColor = Color(0xFF1B5E20) // A dark green color

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Information", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF0F4F0)) // Light green-ish background
                .padding(paddingValues)
                .padding(8.dp) // Add padding around the list
        ) {
            items(departmentList) { department ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = department.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = greenColor,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Email: ${department.email}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Phone: ${department.phone}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Office: ${department.office}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampusInfoScreenPreview() {
    SmartCampusCompanionTheme {
        CampusInfoScreen()
    }
}
