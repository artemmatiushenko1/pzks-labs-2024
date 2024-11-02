package org.example

data class CompilationResult(val errors: List<CompilationError>, val tree: TreeNode?)
