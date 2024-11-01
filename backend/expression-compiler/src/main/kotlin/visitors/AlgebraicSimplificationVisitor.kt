package org.example.visitors

import org.example.parser.*
import org.example.syntaxAnalyzer.SyntaxError

class AlgebraicSimplificationVisitor : Visitor {
    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        return expression
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        val simplifiedExpression = expression.expression.accept(this)

        if (simplifiedExpression is NumberLiteralExpression && simplifiedExpression.value == "0") {
            return simplifiedExpression
        }

        return ParenExpression(expression = simplifiedExpression)
    }

    private fun isMultiplicationByZero(left: Expression, right: Expression, operator: String): Boolean {
        return operator == "*" &&
                ((left is NumberLiteralExpression && left.value == "0") ||
                        (right is NumberLiteralExpression && right.value == "0"))
    }

    private fun simplifyMultiplicationByZero(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val left = binaryExpression.left
        val right = binaryExpression.right

        if (isMultiplicationByZero(left, right, operator)) {
            return NumberLiteralExpression("0")
        }

        return BinaryExpression(left = left, right = right, operator = operator)
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val operator = expression.operator
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)

        val simplifiers = listOf(
            AlgebraicSimplificationVisitor::simplifyMultiplicationByZero,
        )

        return simplifiers.fold(
            BinaryExpression(
                left = left,
                right = right,
                operator = operator
            ) as Expression
        ) { expr, simplify -> if (expr is BinaryExpression) simplify.invoke(this, expr) else expr }
    }
}
