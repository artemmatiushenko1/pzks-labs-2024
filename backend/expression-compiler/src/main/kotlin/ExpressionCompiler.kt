package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.Parser
import org.example.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl

class ExpressionCompiler {
    private val optimizer = Optimizer()

    fun compile(expression: String): CompilationResult {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()

        val serializableTree = if (syntaxErrors.isEmpty()) {
            Parser(tokens = tokens).parse()?.let {
                val optimizedAst = optimizer.optimize(it)
                val visitor = ToSerializableTreeVisitor()
                optimizedAst.accept(visitor)
                visitor.getTree()
            }
        } else null

        return CompilationResult(
            syntaxErrors = syntaxErrors,
            tree = serializableTree
        ) // TODO: also send unoptimised tree
    }
}
