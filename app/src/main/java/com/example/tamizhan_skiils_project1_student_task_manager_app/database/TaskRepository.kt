package com.example.tamizhan_skiils_project1_student_task_manager_app.database

import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }
}