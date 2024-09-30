package org.example

interface LexicalAnalyzer {
    val expressionSource: String

    fun tokenize(): List<Token>
}
