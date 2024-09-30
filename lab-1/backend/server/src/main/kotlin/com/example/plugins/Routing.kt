package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.ExpressionCompiler
import org.example.SyntaxError
import javax.xml.crypto.Data

@Serializable
data class CompileRequestBody(val expression: String)

@Serializable
data class SyntaxErrorDto(val message: String?, val position: Int?)
@Serializable
data class CompileResponseBody(val syntaxErrors: List<SyntaxErrorDto>)

fun Application.configureRouting() {
    routing {
        post("/compile") {
            val requestBody = call.receive<CompileRequestBody>()
            val syntaxErrors = ExpressionCompiler().compile(requestBody.expression)
            call.respond(CompileResponseBody(syntaxErrors = syntaxErrors.map { SyntaxErrorDto(message = it.message, position = it.position) }))
        }
    }
}
