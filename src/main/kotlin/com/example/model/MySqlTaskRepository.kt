package com.example.model

import com.example.db.TaskDao
import com.example.db.TaskTable
import com.example.db.daoToModel
import com.example.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class MySqlTaskRepository: TaskRepository {
    override suspend fun allTasks(): List<Task> {
        return suspendTransaction {
            TaskDao.all().map { daoToModel(it) }
        }
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return suspendTransaction {
            TaskDao
                .find { TaskTable.priority eq priority.toString() }
                .map { daoToModel(it) }
        }
    }

    override suspend fun taskByName(name: String): Task? {
        return suspendTransaction {
            TaskDao
                .find { TaskTable.name eq name }
                .limit(1)
                .map { daoToModel(it) }
                .firstOrNull()
        }
    }

    override suspend fun addTask(task: Task) {
        TaskDao.new {
            name = task.name
            description = task.description
            priority = task.priority.toString()
        }
    }

    override suspend fun deleteTaskByName(name: String): Boolean {
        val taskDeleted = TaskTable.deleteWhere {
            TaskTable.name eq name
        }
        return taskDeleted == 1
    }
}