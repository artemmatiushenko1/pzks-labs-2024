package org.example

interface SyntaxAnalyzer {
    fun analyze(tokens: List<Token>): List<SyntaxError>
}
