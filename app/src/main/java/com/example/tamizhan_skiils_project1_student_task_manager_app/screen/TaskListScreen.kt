package com.example.tamizhan_skiils_project1_student_task_manager_app.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task
import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.getPriorityValue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavHostController) {
    val taskList = remember { mutableStateListOf<Task>() }
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var currentScreen by remember { mutableStateOf("tasks") }
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Filter by Category", modifier = Modifier.padding(16.dp))
                listOf("All", "Academic", "Personal", "Other").forEach { category ->
                    NavigationDrawerItem(
                        label = { Text(category) },
                        selected = selectedCategory == category || (category == "All" && selectedCategory == null),
                        onClick = {
                            selectedCategory = if (category == "All") null else category
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Student Task Manager") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    editingTask = null
                    showDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                        label = { Text("Tasks") },
                        selected = currentScreen == "tasks",
                        onClick = { currentScreen = "tasks" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                        label = { Text("Completed") },
                        selected = currentScreen == "completed",
                        onClick = { currentScreen = "completed" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") },
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val filteredTasks = taskList.filter {
                    (selectedCategory == null || it.category == selectedCategory) &&
                            when (currentScreen) {
                                "tasks" -> !it.isDone
                                "completed" -> it.isDone
                                else -> true
                            }
                }

                when (currentScreen) {
                    "tasks", "completed" -> LazyColumn {
                        items(filteredTasks) { task ->
                            TaskCard(task) {
                                editingTask = task
                                showDialog = true
                            }
                        }
                    }
                    "profile" -> {
                        Text("\uD83D\uDC64 Profile Coming Soon...", modifier = Modifier.padding(16.dp))
                    }
                }
            }

            if (showDialog) {
                AddTaskDialog(
                    task = editingTask,
                    onAdd = { newTask ->
                        if (editingTask != null) {
                            val index = taskList.indexOfFirst { it.id == newTask.id }
                            if (index != -1) taskList[index] = newTask
                        } else {
                            taskList.add(newTask)
                        }
                        taskList.sortWith(compareBy { getPriorityValue(it.priority) })
                        showDialog = false
                        editingTask = null
                    },
                    onDismiss = {
                        showDialog = false
                        editingTask = null
                    }
                )
            }
        }
    }
}


// val c2 = Color(0xFFB65B7C)
//    val c3 = Color(0xFF5F96C5)

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    val c2 = Color(0xFFB65B7C)
    val c3 = Color(0xFF5F96C5)
    val cardColor = if (task.category == "Personal") c2 else c3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Due: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(task.dueDate))}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Priority: ${task.priority}",
                style = MaterialTheme.typography.labelMedium,
                color = when (task.priority) {
                    "High" -> Color.Red
                    "Medium" -> Color(0xFFFFA500) // Orange
                    else -> Color.Black
                }
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onAdd: (Task) -> Unit,
    onDismiss: () -> Unit,
    task: Task? = null
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var category by remember { mutableStateOf("Academic") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isDone by remember { mutableStateOf(false) }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Academic", "Personal", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    onAdd(
                        Task(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            priority = priority,
                            category = category,
                            dueDate = dueDate,
                            isDone = isDone
                        )
                    )
                }
            }) { Text("Add") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector("Priority", priorities, priority) { priority = it }
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector("Category", categories, category) { category = it }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDone, onCheckedChange = { isDone = it })
                    Text("Completed")
                }
                Spacer(modifier = Modifier.height(8.dp))
                DateSelector(dueDate) { dueDate = it }
            }
        }
    )
}

@Composable
fun DropdownSelector(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selected)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateSelector(currentDate: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = currentDate } }

    Button(onClick = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        Text("Pick Due Date")
    }
}