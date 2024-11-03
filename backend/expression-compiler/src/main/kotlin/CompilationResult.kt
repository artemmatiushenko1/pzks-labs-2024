package org.example

import kotlinx.serialization.Serializable

@Serializable
data class CompilationResult(
    val errors: List<CompilationError>,
    val tree: TreeNode? = null,
    val optimizedTree: TreeNode? = null,
    val originalTree: TreeNode? = null,
    val originalExpressionString: String? = null,
    val optimizedExpressionString: String? = null
)
