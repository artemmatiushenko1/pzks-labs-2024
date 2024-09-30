package org.example

internal enum class TokenType {
    IDENTIFIER, // "a", "b"
    OPEN_PAREN, // "("
    CLOSE_PAREN, // ")"
    MATH_OPERATOR, // "=", "+", "-"
    NUMBER, // "1", "2", "345", "5.616", "4.32"
    // TODO: add sin, cos etc.
}
