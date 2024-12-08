package org.example

import org.example.parser.Expression

data class HistoryEntry(
    val processingUnitId: String,
    val state: ProcessingUnit.State,
    val task: Task,
    val time: Int = 1,
)

class VectorSystem(
    private val expression: Expression,
) {
    private var time = 1;
    private val completedTaskIds = mutableSetOf<Int>()
    private val history = mutableListOf<HistoryEntry>()
    private var isReadWriteBlocked = false // TODO: handle read write block

    private val processingUnits: List<ProcessingUnit> = listOf(
        ProcessingUnit(id = "P[+,-]1", types = listOf(TaskType.SUM, TaskType.SUBTRACTION)),
        ProcessingUnit(
            id = "P[*,/]1",
            types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
        ),
        ProcessingUnit(
            id = "P[*,/]2",
            types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
        )
    )

    private fun produceTasks(): List<Task> {
        val toTasksVisitor = ToTasksVisitor()
        expression.accept(toTasksVisitor)
        val tasks = toTasksVisitor.getTasks()
        return tasks
    }

    fun getHistory(): List<HistoryEntry> {
        return this.history
    }

    private fun nextTick() {
        val activeUnits = processingUnits.filter { it.task != null }

        for (activeUnit in activeUnits) {
            val historyEntry = HistoryEntry(
                processingUnitId = activeUnit.id,
                state = activeUnit.state,
                task = activeUnit.task!!,
                time = time
            )

            if (activeUnit.state != ProcessingUnit.State.IDLE) {
                this.addHistoryEntry(historyEntry)
            }

            val newTimeLeft = activeUnit.timeLeft - 1

            when (activeUnit.state) {
                ProcessingUnit.State.READING -> {
                    val newDependenciesToRead = activeUnit.dependenciesToRead - 1

                    if (newDependenciesToRead <= 0) {
                        activeUnit.state = ProcessingUnit.State.PROCESSING
                    } else {
                        activeUnit.dependenciesToRead = newDependenciesToRead
                    }
                }

                ProcessingUnit.State.PROCESSING -> {
                    if (newTimeLeft == 0) {
                        if (!isReadWriteBlocked) {
                            this.isReadWriteBlocked = true
                            activeUnit.state = ProcessingUnit.State.WRITING
                        } else {
                            activeUnit.state = ProcessingUnit.State.IDLE
                        }
                    }

                    activeUnit.timeLeft = newTimeLeft
                }

                ProcessingUnit.State.WRITING -> {
                    completedTaskIds.add(activeUnit.task!!.id)
                    activeUnit.state = ProcessingUnit.State.IDLE
                    activeUnit.task = null
                    activeUnit.timeLeft = 0
                }

                ProcessingUnit.State.IDLE -> {
                    if (!isReadWriteBlocked && activeUnit.timeLeft == 0 && activeUnit.task != null) {
                        activeUnit.state = ProcessingUnit.State.WRITING
                    }
                }
            }
        }

        time++
    }

    private fun addHistoryEntry(entry: HistoryEntry) {
        this.history.add(entry)
    }

    fun process() {
        val tasks = produceTasks().toMutableList()

        while (tasks.isNotEmpty() || processingUnits.any { it.state != ProcessingUnit.State.IDLE || it.task != null }) {
            val task = tasks.firstOrNull()

            this.isReadWriteBlocked = false

            if (task == null) {
                this.nextTick()
                continue
            }

            val areAllDependenciesCompleted = task.dependencies.all { it.id in completedTaskIds }

            if (!areAllDependenciesCompleted) {
                this.nextTick() // TODO: maybe try the next task that is not blocked?
                continue
            }

            val availableProcessingUnit = processingUnits.firstOrNull {
                it.task == null && task.type in it.types
            }

            if (availableProcessingUnit != null) {
                val isReadWriteBlocked = processingUnits.any {
                    it.state in listOf(
                        ProcessingUnit.State.WRITING,
                        ProcessingUnit.State.READING
                    )
                }

                if (!isReadWriteBlocked) {
                    tasks.remove(task)
                    availableProcessingUnit.assignTask(task)
                }
            }

            this.nextTick()
        }
    }
}
