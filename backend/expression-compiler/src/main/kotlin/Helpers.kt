package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.Expression
import org.example.parser.Parser

fun generateAst(expressionSource: String): Expression? {
    val tokens = LexicalAnalyzerImpl(expressionSource = expressionSource).tokenize()
    val ast = Parser(tokens = tokens).parse()
    return ast
}
