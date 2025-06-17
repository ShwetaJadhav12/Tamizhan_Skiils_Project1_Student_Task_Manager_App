package com.example.tamizhan_skiils_project1_student_task_manager_app.Navigation

import AddEditTaskScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.AppDatabase
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.TaskRepository
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.TaskViewModel
import com.example.tamizhan_skiils_project1_student_task_manager_app.database.TaskViewModelFactory
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.TaskListScreen



@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val dao = AppDatabase.getDatabase(context).taskDao()
    val repository = TaskRepository(dao)
    val viewModelFactory = TaskViewModelFactory(repository)

    NavHost(navController = navController, startDestination = "task_list") {

        composable("task_list") {
            val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)
            TaskListScreen(navController, viewModel)
        }

        composable("add_edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)
            AddEditTaskScreen(navController = navController, taskId = taskId, viewModel = viewModel)
        }

        composable("add_edit_task") {
            val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)
            AddEditTaskScreen(navController = navController, taskId = null, viewModel = viewModel)
        }
    }
}
