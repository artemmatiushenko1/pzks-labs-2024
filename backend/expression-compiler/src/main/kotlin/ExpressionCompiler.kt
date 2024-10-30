package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.Parser
import org.example.parser.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl

class ExpressionCompiler {
    fun compile(expression: String): CompilationResult {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()
        
        val serializableTree = if (syntaxErrors.isEmpty()) {
            val ast = Parser(tokens = tokens).parse()
            val visitor = ToSerializableTreeVisitor()
            ast.expression?.accept(visitor)
            visitor.getTree()
        } else null

        return CompilationResult(syntaxErrors = syntaxErrors, tree = serializableTree)
    }
}
