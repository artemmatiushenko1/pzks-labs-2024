package org.example

class ProcessingUnit(
    val id: String,
    val types: List<TaskType>,
    var state: State = State.IDLE,
    var task: Task? = null,
    var timeLeft: Int = 0,
    var dependenciesToRead: Int = 0,
) {
    enum class State {
        IDLE, READING, PROCESSING, WRITING
    }

    private fun read() {
        this.state = State.READING
    }

    fun process() {
        this.state = State.PROCESSING
    }

    fun write() {
        this.state = State.WRITING
        this.task = null
        this.idle()
    }

    private fun idle() {
        this.state = State.IDLE
    }

    fun assignTask(task: Task) {
        this.task = task
        this.timeLeft = task.type.duration
        this.dependenciesToRead = task.dependencies.count()
        this.read()
    }
}
