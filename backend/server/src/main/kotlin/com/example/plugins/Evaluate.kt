package com.example.plugins

import kotlinx.serialization.Serializable
import org.example.ExpressionCompiler
import org.example.ProcessingUnit
import org.example.VectorSystem

@Serializable
data class EvaluateExpressionRequest(val expression: String)

@Serializable
data class SerializableHistoryEntry(
    val processingUnitId: String,
    val instruction: String,
    val time: Int,
    val state: ProcessingUnit.State
)

@Serializable
data class EvaluateExpressionResponse(val entries: List<SerializableHistoryEntry>)

fun evaluateExpression(request: EvaluateExpressionRequest): EvaluateExpressionResponse {
    val optimizedExpression = ExpressionCompiler().produceOptimizedAst(request.expression)
        ?: throw Exception("Failed to compile expression!")

    val vectorSystem = VectorSystem(optimizedExpression)
    vectorSystem.process()

    val history = vectorSystem.getHistory()
    val response =
        EvaluateExpressionResponse(entries = history.map {
            SerializableHistoryEntry(
                processingUnitId = it.processingUnitId,
                instruction = it.instruction.toString(),
                time = it.time,
                state = it.state
            )
        })

    return response
}
