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
data class CompilationError(val message: String?, val position: Int?, val type: String)

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequestBody>()
            try {
                val syntaxErrors = ExpressionCompiler().compile(requestBody.expression)
                call.respond(syntaxErrors.map { CompilationError(message = it.message, position = it.position, type = "SyntaxError") })
            } catch (e: LexicalError) {
                call.respond(listOf(CompilationError(message = e.message, position = e.position, type = "LexicalError")))
            }
        }
    }
}
