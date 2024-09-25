package org.example

enum class TokenType {
    IDENTIFIER, // "a", "b"
    OPEN_PAREN, // "("
    CLOSE_PAREN, // ")"
    MATH_OPERATOR, // "=", "+", "-"
    INTEGER, // "1", "2", "345"
    FLOAT, // "5.616", "4.32",
    // TODO: add sin, cos etc.
}
