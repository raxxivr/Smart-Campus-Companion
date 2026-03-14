package com.example.smartcampuscompanion.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.smartcampuscompanion.ui.components.BottomNavBar
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    viewModel: TaskViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onCampusClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Activity", "Quiz", "Exam", "Meeting", "Personal")

    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Task Manager", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedIndex = 2,
                onHomeClick = onHomeClick,
                onAnnouncementsClick = onAnnouncementsClick,
                onTasksClick = { /* Already here */ },
                onCampusClick = onCampusClick,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    taskToEdit = null
                    showDialog = true 
                },
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFBFBFF))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Progress", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                            Text(
                                "$completedCount of $totalCount tasks completed",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                // Categories Row
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryTab(
                                title = category,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        val filteredCount = if (selectedCategory == "All") tasks.size else tasks.count { it.category == selectedCategory }
                        Text("$filteredCount tasks", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }

                // Tasks List
                val filteredTasks = if (selectedCategory == "All") tasks else tasks.filter { it.category == selectedCategory }
                
                if (filteredTasks.isEmpty()) {
                    item {
                        EmptyCategoryState(selectedCategory)
                    }
                } else {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemModern(
                            task = task,
                            onToggle = { viewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) },
                            onDelete = { taskToDelete = task },
                            onEdit = { 
                                taskToEdit = task
                                showDialog = true 
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            TaskDialog(
                task = taskToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { title, desc, category, date ->
                    if (taskToEdit == null) {
                        viewModel.addTask(title, desc, date, category)
                    } else {
                        viewModel.updateTask(taskToEdit!!.copy(title = title, description = desc, category = category, dueDate = date))
                    }
                    showDialog = false
                }
            )
        }

        if (taskToDelete != null) {
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete '${taskToDelete?.title}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTask(taskToDelete!!)
                        taskToDelete = null
                    }) { Text("Delete", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { taskToDelete = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun CategoryTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) TealPrimary else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            color = if (isSelected) Color.White else Color.Gray,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun EmptyCategoryState(category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (category == "All") "No tasks found" else "No tasks in '$category'",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap the + button to add one",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TaskItemModern(task: Task, onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val containerColor = if (task.isCompleted) Color(0xFFF5F5F5) else Color.White
    val contentAlpha = if (task.isCompleted) 0.5f else 1f
    val borderColor = if (task.isCompleted) Color.LightGray else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !task.isCompleted) { onEdit() }
            .background(containerColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (task.isCompleted) TealPrimary else Color.Transparent)
                    .border(2.dp, if (task.isCompleted) TealPrimary else Color.LightGray, RoundedCornerShape(6.dp))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) Color.Gray else Color.Black,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = contentAlpha),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray.copy(alpha = contentAlpha), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(task.dueDate)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = contentAlpha)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(if (task.isCompleted) Color.LightGray.copy(alpha = 0.3f) else TealPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(task.category, style = MaterialTheme.typography.labelSmall, color = if (task.isCompleted) Color.Gray else TealPrimary, fontSize = 10.sp)
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.4f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var desc by remember { mutableStateOf(task?.description ?: "") }
    var category by remember { mutableStateOf(task?.category ?: "Major") }
    
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = task?.dueDate ?: System.currentTimeMillis() } }
    var date by remember { mutableLongStateOf(calendar.timeInMillis) }
    
    val categories = listOf("Activity", "Quiz", "Exam", "Meeting", "Personal")
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = Calendar.getInstance().apply {
                        timeInMillis = datePickerState.selectedDateMillis ?: date
                    }
                    calendar.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                    date = calendar.timeInMillis
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    date = calendar.timeInMillis
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add New Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(date)))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc, category, date) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(if (task == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
