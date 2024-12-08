package org.example

class Task(val type: TaskType, val id: Int, val dependencies: List<Task> = listOf()) {
    override fun toString(): String {
        return "[${type.operator}]$id (dependencies: ${dependencies.map { it.id }})"
    }
}
