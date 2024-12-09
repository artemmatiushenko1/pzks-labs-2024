package com.example.plugins

import kotlinx.serialization.Serializable
import org.example.CompilationResult
import org.example.ExpressionCompiler

@Serializable
data class CompileRequest(val expression: String)

fun compile(request: CompileRequest): CompilationResult {
    val result = ExpressionCompiler().compile(request.expression)
    return result
}
