package com.example.tamizhan_skiils_project1_student_task_manager_app.Data

data class Task(
    val id: String = "0",
    val title: String,
    val description: String,
    val priority: String,
    val category: String,
    val dueDate: Long,
    val isDone: Boolean = false
)

fun getPriorityValue(priority: String): Int = when (priority) {
    "High" -> 1
    "Medium" -> 2
    "Low" -> 3
    else -> 4
}