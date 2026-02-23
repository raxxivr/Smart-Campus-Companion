package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarModuleScreen(
    taskViewModel: TaskViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by taskViewModel.allTasks.collectAsState()
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBFBFF))
        ) {
            MonthSelector(
                selectedDate = selectedDate,
                onMonthChange = { selectedDate = it }
            )
            
            CalendarGrid(
                selectedDate = selectedDate,
                tasks = tasks,
                onDateSelected = { selectedDate = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
            
            Text(
                text = "Tasks for " + SimpleDateFormat("MMMM dd", Locale.getDefault()).format(selectedDate.time),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            val filteredTasks = tasks.filter { isSameDay(it.dueDate, selectedDate.timeInMillis) }
            
            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks for this day", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks) { task ->
                        TaskItemModern(
                            task = task,
                            onToggle = { taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) },
                            onDelete = { taskViewModel.deleteTask(task) },
                            onEdit = { /* Navigate to edit if needed */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthSelector(selectedDate: Calendar, onMonthChange: (Calendar) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val newDate = (selectedDate.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            onMonthChange(newDate)
        }) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }
        
        Text(
            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = {
            val newDate = (selectedDate.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            onMonthChange(newDate)
        }) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
    }
}

@Composable
fun CalendarGrid(selectedDate: Calendar, tasks: List<Task>, onDateSelected: (Calendar) -> Unit) {
    val daysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (selectedDate.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK) - 1
    
    val days = mutableListOf<Calendar?>()
    repeat(firstDayOfMonth) { days.add(null) }
    for (i in 1..daysInMonth) {
        val day = (selectedDate.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, i) }
        days.add(day)
    }

    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")
    
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val rows = days.chunked(7)
        rows.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                week.forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (day != null) {
                            val isSelected = isSameDay(day.timeInMillis, selectedDate.timeInMillis)
                            val hasTasks = tasks.any { isSameDay(it.dueDate, day.timeInMillis) }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) TealPrimary else Color.Transparent)
                                    .clickable { onDateSelected(day) },
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.get(Calendar.DAY_OF_MONTH).toString(),
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (hasTasks) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else TealPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
                // Fill empty slots if week is shorter than 7
                repeat(7 - week.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
