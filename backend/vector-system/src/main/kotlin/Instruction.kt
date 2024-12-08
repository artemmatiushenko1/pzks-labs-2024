package org.example

class Instruction(val type: InstructionType, val id: Int, val parentInstructions: List<Instruction>? = null) {
    override fun toString(): String {
        return "[${type.operator}]$id (parentIds: ${parentInstructions?.map { it.id }})"
    }
}
