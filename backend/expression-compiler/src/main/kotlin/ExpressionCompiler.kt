package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.ExpressionStatement
import org.example.parser.Parser
import org.example.visitors.ConstantFoldingVisitor
import org.example.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl
import org.example.visitors.AlgebraicSimplificationVisitor
import sun.nio.ch.Net.accept

class ExpressionCompiler {
    private val optimizer = Optimizer()

    fun compile(expression: String): CompilationResult {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()

        val serializableTree = if (syntaxErrors.isEmpty()) {
            val ast = Parser(tokens = tokens).parse()
            val optimizedAst = optimizer.optimize(ast)

            val visitor = ToSerializableTreeVisitor()
            optimizedAst.expression?.accept(visitor)
            visitor.getTree()
        } else null

        return CompilationResult(
            syntaxErrors = syntaxErrors,
            tree = serializableTree
        ) // TODO: also send unoptimised tree
    }
}
