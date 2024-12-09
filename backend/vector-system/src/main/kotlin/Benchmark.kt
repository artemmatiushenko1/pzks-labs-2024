package org.example

import org.example.parser.Expression

class Benchmark {
    private fun evaluateSequentialProcessing(expression: Expression): List<HistoryEntry> {
        val system = VectorSystem(expression)

        system.setProcessingUnits {
            listOf(
                ProcessingUnit(
                    id = "P",
                    types = listOf(TaskType.SUM, TaskType.SUBTRACTION, TaskType.MULTIPLICATION, TaskType.DIVISION),
                    system = it
                )
            )
        }

        system.evaluate()

        return system.getHistory()
    }

    private fun evaluateParallelProcessing(expression: Expression): List<HistoryEntry> {
        val system = VectorSystem(expression)

        system.setProcessingUnits {
            listOf(
                ProcessingUnit(id = "P[+,-]1", types = listOf(TaskType.SUM, TaskType.SUBTRACTION), system = it),
                ProcessingUnit(
                    id = "P[*,/]1",
                    types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
                    system = it,
                ),
                ProcessingUnit(
                    id = "P[*,/]2",
                    types = listOf(TaskType.MULTIPLICATION, TaskType.DIVISION),
                    system = it,
                )
            )
        }

        system.evaluate()

        return system.getHistory()
    }

    fun run(expressionString: String): SystemSpecs {
        val expression = ExpressionCompiler().produceOptimizedAst(expressionString)
        require(expression != null)

        val parallelProcessingHistory = evaluateParallelProcessing(expression)
        val sequentialProcessingHistory = evaluateSequentialProcessing(expression)

        val sequentialTime = sequentialProcessingHistory.maxBy { it.time }.time
        val parallelTime = parallelProcessingHistory.maxBy { it.time }.time

        val parallelProcessingUnitsCount = parallelProcessingHistory
            .groupingBy { it.processingUnitId }
            .eachCount()
            .keys
            .count()

        val acceleration = sequentialTime.toDouble() / parallelTime.toDouble()
        val efficiency = acceleration / parallelProcessingUnitsCount

        return SystemSpecs(
            efficiency = efficiency,
            acceleration = acceleration,
            sequentialProcessingTime = sequentialTime,
            parallelProcessingTime = parallelTime,
            processingUnitsCount = parallelProcessingUnitsCount,
            parallelProcessingHistory = parallelProcessingHistory,
        )
    }
}
