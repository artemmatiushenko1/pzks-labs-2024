package org.example

enum class InstructionType(val operator: String, val duration: Int) {
    SUM("+", 2),
    SUBTRACTION("-", 3),
    MULTIPLICATION("*", 4),
    DIVISION("/", 8)
}
