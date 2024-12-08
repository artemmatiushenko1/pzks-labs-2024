package org.example

import org.example.parser.Expression

data class HistoryEntry(
    val processingUnitId: String,
    val state: ProcessingUnit.State,
    val instruction: Instruction,
    val time: Int = 1,
)

class VectorSystem(
    private val expression: Expression,
) {
    private var time = 1;
    private val completedInstructionIds = mutableSetOf<Int>()
    private val history = mutableListOf<HistoryEntry>()

    private val processingUnits: List<ProcessingUnit> = listOf(
        ProcessingUnit(id = "P[+,-]1", types = listOf(InstructionType.SUM, InstructionType.SUBTRACTION)),
        ProcessingUnit(
            id = "P[*,/]1",
            types = listOf(InstructionType.MULTIPLICATION, InstructionType.DIVISION),
        ),
        ProcessingUnit(
            id = "P[*,/]2",
            types = listOf(InstructionType.MULTIPLICATION, InstructionType.DIVISION),
        )
    )

    private fun produceInstructions(): List<Instruction> {
        val toInstructionsVisitor = ToInstructionsVisitor()
        expression.accept(toInstructionsVisitor)
        val instructions = toInstructionsVisitor.getInstructions()
        return instructions
    }

    fun setProcessingUnits(callback: (system: VectorSystem) -> Unit) {
        callback(this)
    }

    fun getHistory(): List<HistoryEntry> {
        return this.history
    }

    private fun nextTick() {
        val activeUnits = processingUnits.filter { it.instruction != null }

        for (activeUnit in activeUnits) {
            val historyEntry = HistoryEntry(
                processingUnitId = activeUnit.id,
                state = activeUnit.state,
                instruction = activeUnit.instruction!!,
                time = time
            )

            this.addHistoryEntry(historyEntry)

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
                        activeUnit.state = ProcessingUnit.State.WRITING
                    } else {
                        activeUnit.timeLeft = newTimeLeft
                    }
                }

                ProcessingUnit.State.WRITING -> {
                    completedInstructionIds.add(activeUnit.instruction!!.id)
                    activeUnit.state = ProcessingUnit.State.IDLE
                    activeUnit.instruction = null
                    activeUnit.timeLeft = 0
                }

                else -> throw Exception("Invalid state!")
            }
        }

        time++
    }

    fun addHistoryEntry(entry: HistoryEntry) {
        this.history.add(entry)
    }

    fun process() {
        val instructions = produceInstructions().toMutableList()

        while (instructions.isNotEmpty() || processingUnits.any { it.state != ProcessingUnit.State.IDLE }) {
            val instruction = instructions.firstOrNull()

            if (instruction == null) {
                this.nextTick()
                continue
            }

            val areAllDependenciesCompleted =
                instruction.dependencies.all { it.id in completedInstructionIds }

            if (!areAllDependenciesCompleted) {
                this.nextTick()
                continue
            }

            val availableProcessingUnit = processingUnits.firstOrNull {
                it.instruction == null && instruction.type in it.types
            }

            if (availableProcessingUnit != null) {
                instructions.remove(instruction)

                availableProcessingUnit.assignInstruction(instruction)
            }

            this.nextTick()
        }
    }
}
