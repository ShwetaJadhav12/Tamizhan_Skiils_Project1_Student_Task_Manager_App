import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.TaskViewModel
import com.example.tamizhan_skiils_project1_student_task_manager_app.reminder.scheduleTaskReminder
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.DatePickerWithButton
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.DropdownSelector
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskId: Int? = null,
    viewModel: TaskViewModel
) {
    val context = LocalContext.current
    val c1 = Color(0xFFB65B7C)
    var title by remember { mutableStateOf("") }
    var taskLoaded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var category by remember { mutableStateOf("Academic") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isDone by remember { mutableStateOf(false) }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Academic", "Personal", "Other")

    LaunchedEffect(taskId) {
        if (taskId != null && !taskLoaded) {
            val task = viewModel.loadTaskByIdNow(taskId)
            task?.let {
                title = it.title
                description = it.description
                priority = it.priority
                category = it.category
                dueDate = it.dueDate
                isDone = it.isDone
                taskLoaded = true
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add / Edit Task") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    val newTask = com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task(
                        id = taskId?: 0,
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        dueDate = dueDate,
                        isDone = isDone
                    )

                    if (taskId != null) {
                        viewModel.updateTask(newTask)
                    } else {
                        viewModel.addTask(newTask)
                    }

                    scheduleTaskReminder(
                        context = context,
                        taskTitle = title,
                        taskDescription = description,
                        timeInMillis = dueDate
                    )
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        },
        containerColor = c1
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
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

            Spacer(modifier = Modifier.height(12.dp))
            DropdownSelector("Priority", priorities, priority) { priority = it }

            Spacer(modifier = Modifier.height(12.dp))
            DropdownSelector("Category", categories, category) { category = it }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}