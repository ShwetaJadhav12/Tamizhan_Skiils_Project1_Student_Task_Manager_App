package com.example.tamizhan_skiils_project1_student_task_manager_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.tamizhan_skiils_project1_student_task_manager_app.Navigation.Navigation
import com.example.tamizhan_skiils_project1_student_task_manager_app.ui.theme.Tamizhan_Skiils_Project1_Student_Task_Manager_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tamizhan_Skiils_Project1_Student_Task_Manager_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        navController = rememberNavController(),

                    )
                }
            }
        }
    }
}

