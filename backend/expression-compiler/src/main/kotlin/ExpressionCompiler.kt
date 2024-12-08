package org.example

import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.lexicalAnalyzer.LexicalError
import org.example.parser.Expression
import org.example.parser.Parser
import org.example.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl
import org.example.visitors.ToStringVisitor

class ExpressionCompiler {
    private val optimizer = Optimizer()

    private fun getCompiledExpressionString(ast: Expression?): String {
        val visitor = ToStringVisitor()
        ast?.accept(visitor)
        return visitor.getExpressionString()
    }

    private fun getSerializableTree(ast: Expression?): TreeNode {
        val visitor = ToSerializableTreeVisitor()
        ast?.accept(visitor)
        return visitor.getTree()
    }

    fun produceOptimizedAst(expression: String): Expression? {
        val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
        val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()

        val ast = if (syntaxErrors.isEmpty()) {
            Parser(tokens = tokens).parse()
        } else null

        val optimizedAst = ast?.let {
            optimizer.optimize(it)
        }

        return optimizedAst
    }

    fun compile(expression: String): CompilationResult {
        try {
            val tokens = LexicalAnalyzerImpl(expressionSource = expression).tokenize()
            val syntaxErrors = SyntaxAnalyzerImpl(tokens = tokens).analyze()

            val ast = if (syntaxErrors.isEmpty()) {
                Parser(tokens = tokens).parse()
            } else null

            val optimizedAst = ast?.let {
                optimizer.optimize(it)
            }

            if (syntaxErrors.isNotEmpty()) {
                return CompilationResult(
                    errors = syntaxErrors.map {
                        CompilationError(
                            message = it.message,
                            position = it.position,
                            type = "SyntaxError"
                        )
                    },
                )
            }

            return CompilationResult(
                errors = emptyList(),
                originalTree = getSerializableTree(ast),
                optimizedTree = getSerializableTree(optimizedAst),
                originalExpressionString = getCompiledExpressionString(ast),
                optimizedExpressionString = getCompiledExpressionString(optimizedAst),
            )
        } catch (e: Exception) {
            return when (e) {
                is LexicalError -> CompilationResult(
                    errors = listOf(
                        CompilationError(
                            message = e.message,
                            position = e.position,
                            type = "LexicalError"
                        )
                    )
                )

                else -> CompilationResult(
                    errors = listOf(
                        CompilationError(
                            message = e.message,
                            position = null,
                            type = "Exception"
                        )
                    )
                )
            }
        }
    }
}
