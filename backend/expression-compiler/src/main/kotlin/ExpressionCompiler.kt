package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.ExpressionStatement
import org.example.parser.Parser
import org.example.visitors.ConstantFoldingVisitor
import org.example.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl

class ExpressionCompiler {
    fun compile(expression: String): CompilationResult {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()

        val serializableTree = if (syntaxErrors.isEmpty()) {
            val ast = Parser(tokens = tokens).parse()
            val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

            val visitor = ToSerializableTreeVisitor()
            foldedAst?.accept(visitor)
            visitor.getTree()
        } else null

        return CompilationResult(
            syntaxErrors = syntaxErrors,
            tree = serializableTree
        ) // TODO: also send unoptimised tree
    }
}
