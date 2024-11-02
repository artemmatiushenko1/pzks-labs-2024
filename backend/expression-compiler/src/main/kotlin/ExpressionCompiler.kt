package org.example

import kotlinx.serialization.Serializable
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.lexicalAnalyzer.LexicalError
import org.example.parser.Parser
import org.example.visitors.ToSerializableTreeVisitor
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl

@Serializable
data class CompilationError(val message: String?, val position: Int?, val type: String)

class ExpressionCompiler {
    private val optimizer = Optimizer()

    fun compile(expression: String): CompilationResult {
        try {
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
                errors = syntaxErrors.map {
                    CompilationError(
                        message = it.message,
                        position = it.position,
                        type = "SyntaxError"
                    )
                },
                tree = serializableTree
            ) // TODO: also send unoptimised tree
        } catch (e: Exception) {
            return when (e) {
                is LexicalError -> CompilationResult(
                    errors = listOf(
                        CompilationError(
                            message = e.message,
                            position = e.position,
                            type = "LexicalError"
                        )
                    ),
                    tree = null
                )

                else -> CompilationResult(
                    errors = listOf(
                        CompilationError(
                            message = e.message,
                            position = null,
                            type = "Exception"
                        )
                    ),
                    tree = null
                )
            }
        }
    }
}
