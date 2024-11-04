package org.example

import org.example.parser.Expression
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.BalanceVisitor
import org.example.visitors.ConstantFoldingVisitor
import org.example.visitors.RedundantParensRemovalVisitor

class Optimizer {
    private val visitors = listOf(
        AlgebraicSimplificationVisitor(),
        ConstantFoldingVisitor(),
        RedundantParensRemovalVisitor(),
        BalanceVisitor(),
    )

    fun optimize(ast: Expression): Expression {
        val optimizedExpression = visitors.fold(ast) { it, visitor ->
            it.accept(visitor)
        }

        return optimizedExpression
    }
}
