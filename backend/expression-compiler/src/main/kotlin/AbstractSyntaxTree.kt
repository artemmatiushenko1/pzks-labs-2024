package org.example

enum class AstNoteType {
    EXPRESSION,
    BINARY_EXPRESSION,
    LETERAL,
    IDENTIFIER,
}

data class AstNode(val type: String)
abstract class AbstractSyntaxTree() {
    fun build() {

    }
}
