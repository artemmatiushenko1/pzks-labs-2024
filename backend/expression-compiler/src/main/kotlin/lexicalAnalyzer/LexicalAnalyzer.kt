package org.example.lexicalAnalyzer

internal interface LexicalAnalyzer {
    val expressionSource: String

    fun tokenize(): List<Token>
}
