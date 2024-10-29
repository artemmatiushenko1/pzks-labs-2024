package org.example

data class CompilationResult(val syntaxErrors: List<SyntaxError>, val tree: TreeNode?)
