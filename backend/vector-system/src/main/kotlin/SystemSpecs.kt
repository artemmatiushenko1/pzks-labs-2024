package org.example

data class SystemSpecs(
    val efficiency: Double,
    val acceleration: Double,
    val sequentialProcessingTime: Int,
    val parallelProcessingTime: Int,
    val processingUnitsCount: Int,
    val parallelProcessingHistory: List<HistoryEntry>,
)
