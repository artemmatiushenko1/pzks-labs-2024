package org.example

class ProcessingUnit(
    val id: String,
    val types: List<TaskType>,
    var state: State = State.IDLE,
    var task: Task? = null,
    val localMemory: MutableSet<Task> = mutableSetOf(),
    val system: VectorSystem,
    private var timeLeft: Int = 0,
    private var dependenciesReadQueue: MutableList<Task> = mutableListOf(),
) {
    enum class State {
        IDLE, READING, PROCESSING, WRITING
    }

    fun handleRead() {
        if (task!!.dependencies.isEmpty()) {
            // initial task read
            this.state = State.PROCESSING
            return
        }

        this.localMemory.add(dependenciesReadQueue.removeLast())

        if (dependenciesReadQueue.isEmpty()) {
            this.state = State.PROCESSING
        }
    }

    fun handleProcessing() {
        val newTimeLeft = this.timeLeft - 1

        if (newTimeLeft == 0) {
            if (!this.system.getIsReadWriteBlocked()) {
                this.system.acquireReadWriteBlock()
                this.state = State.WRITING
            } else {
                state = State.IDLE
            }
        }

        this.timeLeft = newTimeLeft
    }

    fun handleWriting() {
        this.localMemory.add(task!!)
        this.state = State.IDLE
        this.task = null
    }

    fun handleIdle() {
        // continue a write operation that was previously blocked
        if (!this.system.getIsReadWriteBlocked() && timeLeft == 0 && task != null) {
            this.state = State.WRITING
        }
    }

    fun assignTask(task: Task) {
        this.task = task
        this.timeLeft = task.type.duration

        // we don't need to read the task that were already wrote by this unit
        val dependenciesToRead = task.dependencies.filter { it !in localMemory }
        val hasDependencies = task.dependencies.isNotEmpty()

        this.state = if (dependenciesToRead.isEmpty() && hasDependencies) State.PROCESSING else State.READING

        this.dependenciesReadQueue.clear()
        this.dependenciesReadQueue.addAll(dependenciesToRead)
    }
}
