package org.example

enum class TokenType {
    IDENTIFIER, // "a", "b"
    OPEN_PAREN, // "("
    CLOSE_PAREN, // ")"
    NUMBER, // "1", "2", "345", "5.616", "4.32"
    ADDITIVE_OPERATOR, // "+", "-"
    MULTIPLICATIVE_OPERATOR, // "*", "/"
    WHITESPACE,
}
