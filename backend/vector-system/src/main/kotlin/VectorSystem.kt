package org.example

import org.example.parser.Expression

data class HistoryEntry(
    val processingUnitId: String,
    val state: ProcessingUnit.State,
    val task: Task?,
    val time: Int = 1,
)

class VectorSystem(
    private val expression: Expression,
) {
    private var time = 1;

    private val history = mutableListOf<HistoryEntry>()
    private var isReadWriteBlocked = false // TODO: handle read write block

    private val processingUnits: List<ProcessingUnit> = listOf(
        ProcessingUnit(id = "P[+,-]1", types = listOf(TaskType.SUM, TaskType.SUBTRACTION), system = this),
        ProcessingUnit(
            id = "P[*,/]1",
            types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
            system = this,
        ),
        ProcessingUnit(
            id = "P[*,/]2",
            types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
            system = this,
        )
    )

    fun getIsReadWriteBlocked(): Boolean = this.isReadWriteBlocked

    fun acquireReadWriteBlock() = run { this.isReadWriteBlocked = true }

    private fun releaseReadWriteBlock() = run { this.isReadWriteBlocked = false }

    fun getHistory(): List<HistoryEntry> {
        return this.history
    }

    private fun addHistoryEntry(entry: HistoryEntry) {
        this.history.add(entry)
    }

    private fun produceTasks(): List<Task> {
        val toTasksVisitor = ToTasksVisitor()
        expression.accept(toTasksVisitor)
        val tasks = toTasksVisitor.getTasks()
        return tasks
    }

    private fun nextTick() {
        for (unit in processingUnits) {
            val historyEntry = HistoryEntry(
                processingUnitId = unit.id,
                state = unit.state,
                task = unit.task,
                time = time
            )

            if (unit.state != ProcessingUnit.State.IDLE) {
                this.addHistoryEntry(historyEntry)
            }
        }

        val activeUnits = processingUnits.filter { it.task != null }

        for (activeUnit in activeUnits) {
            when (activeUnit.state) {
                ProcessingUnit.State.READING -> activeUnit.handleRead()
                ProcessingUnit.State.PROCESSING -> activeUnit.handleProcessing()
                ProcessingUnit.State.WRITING -> activeUnit.handleWriting()
                ProcessingUnit.State.IDLE -> activeUnit.handleIdle()
            }
        }

        time++
    }

    fun process() {
        val tasksQueue = produceTasks().toMutableList()

        while (tasksQueue.isNotEmpty() || processingUnits.any { it.state != ProcessingUnit.State.IDLE || it.task != null }) {
            this.releaseReadWriteBlock()

            val completedTaskIds = processingUnits.flatMap { it.localMemory }.map { it.id }

            val readyTask = tasksQueue.firstOrNull { task ->
                task.dependencies.all { dependency -> dependency.id in completedTaskIds }
            }

            if (readyTask == null) {
                this.nextTick()
                continue
            }

            val areAllDependenciesCompleted = readyTask.dependencies.all { it.id in completedTaskIds }

            if (!areAllDependenciesCompleted) {
                this.nextTick() // TODO: maybe try the next task that is not blocked?
                continue
            }

            val availableProcessingUnit = processingUnits.firstOrNull {
                it.task == null && readyTask.type in it.types
            }

            if (availableProcessingUnit != null) {
                val isReadWriteBlocked = processingUnits.any {
                    it.state in listOf(
                        ProcessingUnit.State.WRITING,
                        ProcessingUnit.State.READING
                    )
                }

                if (!isReadWriteBlocked) {
                    tasksQueue.remove(readyTask)
                    availableProcessingUnit.assignTask(readyTask)
                }
            }

            this.nextTick()
        }
    }
}
