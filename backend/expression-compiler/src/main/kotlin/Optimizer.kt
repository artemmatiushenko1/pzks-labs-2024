package org.example

import org.example.parser.Expression
import org.example.visitors.*

class Optimizer {
    private val visitors: List<Visitor> = listOf(
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
