package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessingUnit(
    val id: String,
    val types: List<InstructionType>,
    var state: State = State.IDLE
) {
    enum class State {
        IDLE, READ, PROCESS, WRITE
    }

    private fun read(memoryUnit: MemoryUnit) {
        this.state = State.READ
    }

    private fun write(memoryUnit: MemoryUnit) {
        this.state = State.WRITE
    }

    private fun idle() {
        println("Set state IDLE on $id")
        this.state = State.IDLE
    }

    fun process(instruction: Instruction) {
        this.state = State.PROCESS
        println("Set state PROCESS on $id")

        this.idle()
    }
}
