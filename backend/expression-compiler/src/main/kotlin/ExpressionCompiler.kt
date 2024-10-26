package org.example

class ExpressionCompiler {
    fun compile(expression: String): List<SyntaxError> {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()
        return syntaxErrors
    }
}
