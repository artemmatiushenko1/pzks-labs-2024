package org.example

internal interface SyntaxAnalyzer {
    fun analyze(): List<SyntaxError>
}
