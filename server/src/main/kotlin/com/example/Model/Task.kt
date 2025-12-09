package com.example.Model


enum class Priority
{
    Low,Medium,High,Critical
}
data class Task(
    val id:String,
    val descr:String,
    val priority: Priority
)
