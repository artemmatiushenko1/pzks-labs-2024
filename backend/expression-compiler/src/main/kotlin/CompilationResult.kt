package org.example

import org.example.syntaxAnalyzer.SyntaxError

data class CompilationResult(val syntaxErrors: List<SyntaxError>, val tree: TreeNode?)
