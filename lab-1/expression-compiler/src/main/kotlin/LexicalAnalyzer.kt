package org.example

interface LexicalAnalyzer {
    val tokens: List<Token>
    val expressionSource: String

    fun tokenize(): List<Token>
}
