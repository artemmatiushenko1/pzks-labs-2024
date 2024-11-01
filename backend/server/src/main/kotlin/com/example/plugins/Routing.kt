package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.ExpressionCompiler
import org.example.lexicalAnalyzer.LexicalError
import org.example.TreeNode

@Serializable
data class CompileRequest(val expression: String)

@Serializable
data class CompileRequestResponse(val syntaxErrors: List<CompilationError>, val tree: TreeNode?)

@Serializable
data class CompilationError(val message: String?, val position: Int?, val type: String)

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequest>()
            try {
                val result = ExpressionCompiler().compile(requestBody.expression)
                call.respond(
                    CompileRequestResponse(
                        tree = result.tree,
                        syntaxErrors = result.syntaxErrors.map {
                            CompilationError(
                                message = it.message,
                                position = it.position,
                                type = "SyntaxError"
                            )
                        }
                    )
                )
            } catch (e: LexicalError) {
                call.respond(
                    CompileRequestResponse(
                        tree = null,
                        syntaxErrors = listOf(
                            CompilationError(
                                message = e.message,
                                position = e.position,
                                type = "LexicalError"
                            )
                        )
                    )
                )
            }
        }
    }
}
