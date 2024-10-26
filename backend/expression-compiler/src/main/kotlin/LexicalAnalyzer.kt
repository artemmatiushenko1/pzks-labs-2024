package org.example

internal interface LexicalAnalyzer {
    val expressionSource: String

    fun tokenize(): List<Token>
}
