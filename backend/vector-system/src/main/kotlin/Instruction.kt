package org.example

class Instruction(val type: InstructionType, val id: Int, val dependencies: List<Instruction> = listOf()) {
    override fun toString(): String {
        return "[${type.operator}]$id (parentIds: ${dependencies.map { it.id }})"
    }
}
