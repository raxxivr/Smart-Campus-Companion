package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.domain.model.Task
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
    var selectedDayTasks by remember { mutableStateOf<List<Task>?>(null) }
    var selectedDateText by remember { mutableStateOf("") }

    // Prepare a list of months to display (e.g., 6 months back, 12 months forward)
    val monthList = remember {
        val list = mutableListOf<Calendar>()
        val start = Calendar.getInstance().apply { add(Calendar.MONTH, -6) }
        repeat(24) {
            list.add(start.clone() as Calendar)
            start.add(Calendar.MONTH, 1)
        }
        list
    }

    // Scroll to current month initially
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 6)

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = TealPrimary,
                        navigationIconContentColor = TealPrimary
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(monthList) { month ->
                MonthSection(
                    month = month,
                    tasks = tasks,
                    onDateClick = { date, dayTasks ->
                        selectedDateText = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date.time)
                        selectedDayTasks = dayTasks
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (selectedDayTasks != null) {
            TaskViewDialog(
                dateText = selectedDateText,
                tasks = selectedDayTasks!!,
                onDismiss = { selectedDayTasks = null }
            )
        }
    }
}

@Composable
fun MonthSection(month: Calendar, tasks: List<Task>, onDateClick: (Calendar, List<Task>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(month.time),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TealPrimary,
            modifier = Modifier.padding(16.dp)
        )
        
        CalendarFullGrid(
            currentMonth = month,
            tasks = tasks,
            onDateClick = onDateClick
        )
    }
}

@Composable
fun CalendarFullGrid(currentMonth: Calendar, tasks: List<Task>, onDateClick: (Calendar, List<Task>) -> Unit) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (currentMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK) - 1
    
    val days = mutableListOf<Calendar?>()
    repeat(firstDayOfMonth) { days.add(null) }
    for (i in 1..daysInMonth) {
        val day = (currentMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, i) }
        days.add(day)
    }

    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        val rows = days.chunked(7)
        rows.forEach { week ->
            Row(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            .clickable(enabled = day != null) {
                                if (day != null) {
                                    val dayTasks = tasks.filter { isSameDay(it.dueDate, day.timeInMillis) }
                                    onDateClick(day, dayTasks)
                                }
                            }
                    ) {
                        if (day != null) {
                            CalendarDayCell(day, tasks)
                        }
                    }
                }
                if (week.size < 7) {
                    repeat(7 - week.size) { 
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) 
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(day: Calendar, tasks: List<Task>) {
    val isToday = isSameDay(day.timeInMillis, System.currentTimeMillis())
    val dayTasks = tasks.filter { isSameDay(it.dueDate, day.timeInMillis) }

    Column(
        modifier = Modifier.fillMaxSize().padding(2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = day.get(Calendar.DAY_OF_MONTH).toString(),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = if (isToday) Modifier
                .background(Color.Red, CircleShape)
                .size(24.dp)
                .wrapContentSize(Alignment.Center)
            else Modifier.padding(start = 4.dp, top = 2.dp)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            dayTasks.take(3).forEach { task ->
                val barColor = getCategoryColor(task.category)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor.copy(alpha = if (task.isCompleted) 0.3f else 1f))
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (dayTasks.size > 3) {
                Text(
                    text = "+${dayTasks.size - 3} more",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TaskViewDialog(dateText: String, tasks: List<Task>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dateText, fontWeight = FontWeight.Bold, color = TealPrimary) },
        text = {
            if (tasks.isEmpty()) {
                Text("No tasks for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        val categoryColor = getCategoryColor(task.category)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = categoryColor.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(categoryColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = task.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = categoryColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = TealPrimary) }
        }
    )
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Exam" -> Color(0xFFFFB74D) // Orange
        "Quiz" -> Color(0xFF9575CD) // Purple
        "Meeting" -> Color(0xFF4FC3F7) // Light Blue
        "Assignment" -> Color(0xFF81C784) // Green
        else -> TealPrimary
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
