package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskRepository

class SampleTaskRepository: TaskRepository {


    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )
    override suspend fun allTasks(): List<Task> {
        return tasks
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return tasks.filter { it.priority == priority }
    }

    override suspend fun taskByName(name: String) =
        tasks.find { it.name.equals(name, true) }


    override suspend fun addTask(task: Task) {
        if(taskByName(task.name) != null){
            throw IllegalStateException("Duplicate Tasks cannot be entered")
        }
        tasks.add(task)
    }

    override suspend fun deleteTaskByName(name: String): Boolean {
        return tasks.removeIf { it.name.equals(name,true) }
    }
}