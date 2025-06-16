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
import com.example.tamizhan_skiils_project1_student_task_manager_app.reminder.scheduleTaskReminder
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.DatePickerWithButton
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.DropdownSelector
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(navController: NavController) {
    val context = LocalContext.current
    val c1 = Color(0xFFB65B7C)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var category by remember { mutableStateOf("Academic") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Academic", "Personal", "Other")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add / Edit Task") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
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
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

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
            DatePickerWithButton(
                selectedDate = dueDate,
                onDatePicked = { dueDate = it }
            )
        }
    }
}