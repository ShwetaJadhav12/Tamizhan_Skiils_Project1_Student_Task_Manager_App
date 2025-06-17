package com.example.tamizhan_skiils_project1_student_task_manager_app.database

import com.example.tamizhan_skiils_project1_student_task_manager_app.Data.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    val allTasks: Flow<List<Task>> = dao.getAllTasks()

    suspend fun insertTask(task: Task) {
        dao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        dao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        dao.delete(task)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return dao.getTaskById(taskId)
    }
}