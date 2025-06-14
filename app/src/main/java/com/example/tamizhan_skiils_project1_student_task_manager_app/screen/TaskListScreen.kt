package com.example.tamizhan_skiils_project1_student_task_manager_app.screen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
                            TaskCard(
                                task = task,
                                onEdit = {
                                    editingTask = task
                                    showDialog = true
                                },
                                onDelete = {
                                    taskList.remove(task)
                                }
                            )
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

@Composable
fun TaskCard(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cardColor = when (task.priority) {
        "High" -> Color(0xFFFFCDD2) // Light red
        "Medium" -> Color(0xFFFFE0B2) // Light orange
        else -> Color(0xFFFFF9C4) // Light yellow
    }

    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFD32F2F)
        "Medium" -> Color(0xFFF57C00)
        else -> Color(0xFFFBC02D)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = task.category,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF212121)
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF212121)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF212121))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Due: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(task.dueDate))}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Priority: ${task.priority}",
                style = MaterialTheme.typography.labelMedium,
                color = priorityColor
            )
        }
    }
}
@Composable
fun AddTaskDialog(
    task: Task?,
    onAdd: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "Medium") }
    var category by remember { mutableStateOf(task?.category ?: "Academic") }
    var isDone by remember { mutableStateOf(task?.isDone ?: false) }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: System.currentTimeMillis()) }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Academic", "Personal", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        val newTask = Task(
                            id = task?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            priority = priority,
                            category = category,
                            dueDate = dueDate,
                            isDone = isDone
                        )
                        onAdd(newTask)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2), contentColor = Color.White)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1976D2))) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = if (task == null) "Add New Task" else "Edit Task",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1976D2)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        focusedLabelColor = Color(0xFF1976D2)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        focusedLabelColor = Color(0xFF1976D2)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownSelector("Priority", priorities, priority) { priority = it }
                Spacer(modifier = Modifier.height(12.dp))
                DropdownSelector("Category", categories, category) { category = it }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDone, onCheckedChange = { isDone = it })
                    Text("Completed")
                }

                Spacer(modifier = Modifier.height(12.dp))

                DateSelector(dueDate) { dueDate = it }
            }
        },
        containerColor = Color.White
    )
}


@Composable
fun DropdownSelector(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF1976D2)
            )
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun DateSelector(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }

    val dateString = remember(selectedDate) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))
    }

    OutlinedTextField(
        value = dateString,
        onValueChange = {},
        readOnly = true,
        label = { Text("Due Date") },
        modifier = Modifier.fillMaxWidth().clickable {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onDateSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1976D2),
            focusedLabelColor = Color(0xFF1976D2)
        )
    )
}
