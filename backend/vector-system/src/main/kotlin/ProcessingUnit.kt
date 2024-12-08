package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessingUnit(
    val id: String,
    val types: List<InstructionType>,
    var state: State = State.IDLE,
    var instruction: Instruction? = null,
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
        this.instruction = null
        this.idle()
    }

    private fun idle() {
        this.state = State.IDLE
    }

    fun assignInstruction(instruction: Instruction) {
        this.instruction = instruction
        this.timeLeft = instruction.type.duration
        this.dependenciesToRead = instruction.dependencies.count()
        this.read()
    }
}
