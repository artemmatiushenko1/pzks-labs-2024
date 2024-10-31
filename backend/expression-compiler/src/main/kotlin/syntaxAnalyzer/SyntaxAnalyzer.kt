package org.example.syntaxAnalyzer

internal interface SyntaxAnalyzer {
    fun analyze(): List<SyntaxError>
}
