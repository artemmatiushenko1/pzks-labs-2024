package org.example

import org.example.parser.ExpressionStatement
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.ConstantFoldingVisitor
import org.example.visitors.RedundantParensRemovalVisitor

class Optimizer {
    private val visitors = listOf(
        AlgebraicSimplificationVisitor(),
        ConstantFoldingVisitor(),
        RedundantParensRemovalVisitor()
    )

    fun optimize(ast: ExpressionStatement): ExpressionStatement {
        val optimizedExpression = visitors.fold(ast.expression) { expr, visitor ->
            expr?.accept(visitor)
        }

        return ExpressionStatement(expression = optimizedExpression)
    }
}
