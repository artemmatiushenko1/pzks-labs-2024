package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.ExpressionCompiler
import org.example.LexicalError
import org.example.SyntaxError
import javax.xml.crypto.Data

@Serializable
data class CompileRequestBody(val expression: String)

@Serializable
data class CompilationError(val message: String?, val position: Int?)

@Serializable
data class CompileResponseBody(val syntaxErrors: List<CompilationError>)

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequestBody>()
            try {
                val syntaxErrors = ExpressionCompiler().compile(requestBody.expression)
                call.respond(CompileResponseBody(syntaxErrors = syntaxErrors.map { CompilationError(message = it.message, position = it.position) }))
            } catch (e: LexicalError) {
                call.respond(CompileResponseBody(syntaxErrors = listOf(CompilationError(message = e.message, position = e.position))))
            }
        }
    }
}
