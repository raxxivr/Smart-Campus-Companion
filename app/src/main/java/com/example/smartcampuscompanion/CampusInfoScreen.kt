package com.example.smartcampuscompanion


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme

data class Department(val name: String)

val departments = listOf(
    Department("College of Coputing Studies"),
    Department("College of Education"),
    Department("College of Engineering"),
    Department("College of Health and Allied Sciences"),
    Department("College of Arts and Sciences"),
    Department("College of Business, Accountancy and Administration")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Campus Information") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(departments) { department ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = department.name,
                        modifier = Modifier.padding(16.dp)
                    )
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
