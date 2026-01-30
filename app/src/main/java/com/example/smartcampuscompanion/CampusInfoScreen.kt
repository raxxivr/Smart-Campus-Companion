package com.example.smartcampuscompanion


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Campus Information") })
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Departments will be listed here")
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