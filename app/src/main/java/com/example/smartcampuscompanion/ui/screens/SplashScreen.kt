package com.example.smartcampuscompanion.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1000) // Total delay reduced to make the transition faster
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logowithname),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .scale(scale)
        )
    }
}
