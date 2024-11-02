package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.CompilationError
import org.example.ExpressionCompiler
import org.example.lexicalAnalyzer.LexicalError
import org.example.TreeNode

@Serializable
data class CompileRequest(val expression: String)

@Serializable
data class CompileResponse(val errors: List<CompilationError>, val tree: TreeNode?)

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequest>()
            val result = ExpressionCompiler().compile(requestBody.expression)

            call.respond(
                CompileResponse(
                    tree = result.tree,
                    errors = result.errors
                )
            )
        }
    }
}
