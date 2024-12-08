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

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequest>()
            call.respond(compile(requestBody))
        }

        post("/evaluate") {
            val requestBody = call.receive<EvaluateExpressionRequest>()
            call.respond(evaluateExpression(requestBody))
        }
    }
}
