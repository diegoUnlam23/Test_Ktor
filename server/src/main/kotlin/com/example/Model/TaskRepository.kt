package com.example.Model

interface TaskRepository {
    suspend fun allTasks():List<Task>;

}