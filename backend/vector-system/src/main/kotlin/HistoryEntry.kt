package org.example

data class HistoryEntry(
    val processingUnitId: String,
    val state: ProcessingUnit.State,
    val task: Task?,
    val time: Int = 1,
)
