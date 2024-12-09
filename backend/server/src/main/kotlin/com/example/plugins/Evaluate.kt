package com.example.plugins

import kotlinx.serialization.Serializable
import org.example.Benchmark
import org.example.ExpressionCompiler
import org.example.ProcessingUnit
import org.example.VectorSystem

@Serializable
data class EvaluateExpressionRequest(val expression: String)

@Serializable
data class SerializableHistoryEntry(
    val processingUnitId: String,
    val taskId: String?,
    val time: Int,
    val state: ProcessingUnit.State
)

@Serializable
data class SerializableSystemSpecs(
    val efficiency: Double,
    val acceleration: Double,
    val sequentialProcessingTime: Int,
    val parallelProcessingTime: Int,
    val processingUnitsCount: Int,
)

@Serializable
data class EvaluateExpressionResponse(
    val entries: List<SerializableHistoryEntry>,
    val specs: SerializableSystemSpecs,
)

fun evaluateExpression(request: EvaluateExpressionRequest): EvaluateExpressionResponse {
    val benchmark = Benchmark()
    val specs = benchmark.run(request.expression)

    val response = EvaluateExpressionResponse(
        specs = SerializableSystemSpecs(
            efficiency = specs.efficiency,
            acceleration = specs.acceleration,
            parallelProcessingTime = specs.parallelProcessingTime,
            sequentialProcessingTime = specs.sequentialProcessingTime,
            processingUnitsCount = specs.processingUnitsCount,
        ),
        entries = specs.parallelProcessingHistory.map {
            SerializableHistoryEntry(
                processingUnitId = it.processingUnitId,
                taskId = it.task?.getPrettyId(),
                time = it.time,
                state = it.state
            )
        })

    return response
}
