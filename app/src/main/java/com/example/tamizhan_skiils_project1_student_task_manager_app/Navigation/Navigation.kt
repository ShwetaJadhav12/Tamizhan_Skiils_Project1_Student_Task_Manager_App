package com.example.tamizhan_skiils_project1_student_task_manager_app.Navigation

import AddEditTaskScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tamizhan_skiils_project1_student_task_manager_app.screen.TaskListScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController, startDestination = "task_list"){
        composable("task_list"){
            TaskListScreen(navController)
        }
        composable("add_edit_task"){
            AddEditTaskScreen(navController)
        }



    }
}