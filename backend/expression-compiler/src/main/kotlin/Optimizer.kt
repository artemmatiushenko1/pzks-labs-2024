package org.example

import org.example.parser.ExpressionStatement
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.ConstantFoldingVisitor

class Optimizer {
    private val visitors = listOf(AlgebraicSimplificationVisitor(), ConstantFoldingVisitor())

    fun optimize(ast: ExpressionStatement): ExpressionStatement {
        val optimizedExpression = visitors.fold(ast.expression) { expr, visitor ->
            expr?.accept(visitor)
        }

        return ExpressionStatement(expression = optimizedExpression)
    }
}
