package org.example

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.parser.Expression

private val DEFAULT_EVALUATION_UNITS = listOf(
    ProcessingUnit(id = "P[+,-]1", types = listOf(InstructionType.SUM, InstructionType.SUBTRACTION)),
    ProcessingUnit(id = "P[*,/]1", types = listOf(InstructionType.MULTIPLICATION, InstructionType.DIVISION)),
    ProcessingUnit(id = "P[*,/]2", types = listOf(InstructionType.MULTIPLICATION, InstructionType.DIVISION))
)

data class HistoryEntry(val processingUnitId: String, val state: ProcessingUnit.State)

class VectorSystem(
    private val expression: Expression,
    private val processingUnits: List<ProcessingUnit> = DEFAULT_EVALUATION_UNITS
) {
    private val history = mutableListOf<HistoryEntry>()

    private fun produceInstructions(): List<Instruction> {
        val toInstructionsVisitor = ToInstructionsVisitor()
        expression.accept(toInstructionsVisitor)
        val instructions = toInstructionsVisitor.getInstructions()
        return instructions
    }

    fun getHistory(): List<HistoryEntry> {
        return this.history
    }

    fun process() {
        val instructions = produceInstructions().toMutableList()

        while (instructions.isNotEmpty()) {
            val instruction = instructions.first()

            val availableProcessingUnit = processingUnits.firstOrNull {
                it.state == ProcessingUnit.State.IDLE && instruction.type in it.types
            }

            if (availableProcessingUnit != null) {
                instructions.remove(instruction)

                availableProcessingUnit.process(instruction)
            }
        }
    }
}
