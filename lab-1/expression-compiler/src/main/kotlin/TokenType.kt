package org.example

enum class TokenType {
    IDENTIFIER, // "a", "b"
    CONSTANT, // "1", "2", "345"
    PUNCTUATOR, // "(", ")"
    MATH_OPERATOR, // "=", "+", "-"
    KEYWORD, // "sin", "cos"
    INTEGER, // "1", "2", "345"
    FLOAT, // "5.616", "4.32"
}
