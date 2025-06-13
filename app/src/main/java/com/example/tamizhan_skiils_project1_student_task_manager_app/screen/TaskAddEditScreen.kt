package com.example.tamizhan_skiils_project1_student_task_manager_app.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.material.datepicker.DateSelector
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(navController: NavController) {
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
                // Save task - backend later
                navController.popBackStack()
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
            DateSelector(dueDate) { dueDate = it }
        }
    }
}
