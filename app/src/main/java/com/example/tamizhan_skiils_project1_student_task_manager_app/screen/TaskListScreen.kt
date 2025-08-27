package com.example.tamizhan_skiils_project1_student_task_manager_app.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task
import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.getPriorityValue
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    taskId: Int? = null
) {
    val taskList by viewModel.allTasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var currentScreen by remember { mutableStateOf("tasks") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
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
                        showDialog = true     // ✅ Opens the AddTaskDialog
                        editingTask = null    // ✅ Ensures it is in Add Mode
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
                val priorityOrder = mapOf("High" to 1, "Medium" to 2, "Low" to 3)

                val filteredTasks = taskList
                    .filter {
                        (selectedCategory == null || it.category == selectedCategory) &&
                                when (currentScreen) {
                                    "tasks" -> !it.isDone
                                    "completed" -> it.isDone
                                    else -> true
                                }
                    }
                    .sortedBy { priorityOrder[it.priority] ?: 4 }


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
                                    viewModel.deleteTask(task)
                                }
                            )
                        }
                    }

                    "profile" -> {
                        Text("👤 Profile Coming Soon...", modifier = Modifier.padding(16.dp))
                    }
                }
            }

            if (showDialog) {
                AddTaskDialog(
                    task = editingTask,
                    onAdd = { newTask ->
                        if (editingTask != null) {
                            viewModel.updateTask(newTask)
                        } else {
                            viewModel.addTask(newTask)
                        }
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
    val (cardColor, contentColor, categoryColor) = if (task.isDone) {
        Triple(Color(0xFFC8E6C9), Color(0xFF1B5E20), Color(0xFF2E7D32)) // Completed: green
    } else {
        when (task.priority) {
            "High" -> Triple(Color(0xFFFFCDD2), Color(0xFFB71C1C), Color(0xFFC62828))
            "Medium" -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100), Color(0xFFEF6C00))
            else -> Triple(Color(0xFFFFF9C4), Color(0xFF827717), Color(0xFF9E9D24))
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
        // Task Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = task.category.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor
                        )
                    )
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = contentColor)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Due: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(task.dueDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }

        // Tick mark if task is completed
        if (task.isDone) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = Color(0xFF2E7D32),
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    task: Task? = null,
    onAdd: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "Medium") }
    var category by remember { mutableStateOf(task?.category ?: "Academic") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: System.currentTimeMillis()) }
    var isDone by remember { mutableStateOf(task?.isDone ?: false) }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Academic", "Personal", "Other")

    val dialogColor = MaterialTheme.colorScheme.surfaceVariant
    val titleColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {

                        if (title.isNotBlank() && description.isNotBlank()) {
                            val newTask = if (task != null) {
                                // Edit mode – keep existing ID
                                task.copy(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    category = category,
                                    dueDate = dueDate,
                                    isDone = isDone
                                )
                            } else {
                                // Add mode – let Room auto-generate ID
                                Task(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    category = category,
                                    dueDate = dueDate,
                                    isDone = isDone
                                )
                            }

                            onAdd(newTask)
                        }


                }
            ) {
                Text("Save Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = if (task != null) "Edit Task" else "New Task",
                color = titleColor,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)

                Spacer(Modifier.height(12.dp))
                Text("Priority", color = labelColor)
                priorities.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        RadioButton(
                            selected = priority == it,
                            onClick = { priority = it }
                        )
                        Text(it, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Category", color = labelColor)
                categories.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        RadioButton(
                            selected = category == it,
                            onClick = { category = it }
                        )
                        Text(it, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { isDone = it }
                    )
                    Text("Mark as Completed", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        containerColor = dialogColor,
        shape = RoundedCornerShape(20.dp)
    )
}



@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF1976D2)
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DatePickerWithButton(
    selectedDate: Long,
    onDatePicked: (Long) -> Unit
) {
    val context = LocalContext.current
    var internalDate by remember { mutableStateOf(selectedDate) }

    val formatted = remember(internalDate) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(internalDate))
    }

    Button(onClick = {
        val calendar = Calendar.getInstance().apply { timeInMillis = internalDate }

        // Step 1: Pick date
        DatePickerDialog(
            context,
            { _, year, month, day ->
                // After date is selected, now pick time
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(Calendar.YEAR, year)
                pickedCalendar.set(Calendar.MONTH, month)
                pickedCalendar.set(Calendar.DAY_OF_MONTH, day)

                // Step 2: Time picker
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        pickedCalendar.set(Calendar.HOUR_OF_DAY, hour)
                        pickedCalendar.set(Calendar.MINUTE, minute)
                        pickedCalendar.set(Calendar.SECOND, 0)
                        pickedCalendar.set(Calendar.MILLISECOND, 0)

                        internalDate = pickedCalendar.timeInMillis
                        onDatePicked(internalDate)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        Text("Pick Date & Time")
    }

    Spacer(modifier = Modifier.height(12.dp))
    Text("Reminder set for: $formatted", style = MaterialTheme.typography.bodyLarge)
}

