package org.example

import kotlinx.serialization.Serializable

@Serializable
data class CompilationError(val message: String?, val position: Int?, val type: String)
