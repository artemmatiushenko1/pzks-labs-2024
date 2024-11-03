package org.example.visitors

import org.example.parser.*

class BalanceVisitor : Visitor {
    private fun getHeight(node: Expression): Int = when (node) {
        is NumberLiteralExpression -> 1
        is IdentifierExpression -> 1
        is UnaryExpression -> 1 + getHeight(node.argument)
        is BinaryExpression -> 1 + maxOf(getHeight(node.left), getHeight(node.right))
        else -> 0
    }

    private fun rotateRight(y: BinaryExpression): BinaryExpression {
        if (y.left !is BinaryExpression) return y

        val x = y.left
        val t2 = x.right

        if (
            (y.isAddition() && y.left.isAddition()) ||
            (y.isMultiplication() && y.left.isMultiplication())
        ) {
            return BinaryExpression(
                left = x.left,
                operator = x.operator,
                right = BinaryExpression(left = t2, operator = x.operator, right = y.right)
            )
        }

        return y
    }

    private fun rotateLeft(x: BinaryExpression): BinaryExpression {
        if (x.right !is BinaryExpression) return x

        val y = x.right
        val t2 = y.left

        if (
            (x.isAddition() && x.right.isAddition()) ||
            (x.isMultiplication() && x.right.isMultiplication())
        ) {
            return BinaryExpression(
                left = BinaryExpression(left = x.left, operator = x.operator, right = t2),
                operator = x.operator,
                right = y.right
            )
        }

        return x
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        var currentExpression = expression

        while (getHeight(currentExpression.left) > getHeight(currentExpression.right) + 1) {
            val newCurrent = rotateRight(currentExpression)
            if (currentExpression == newCurrent) break
            currentExpression = newCurrent
        }

        while (getHeight(currentExpression.right) > getHeight(currentExpression.left) + 1) {
            val rotatedExpression = rotateLeft(currentExpression)
            if (currentExpression == rotatedExpression) break
            currentExpression = rotatedExpression
        }

        return BinaryExpression(
            left = currentExpression.left.accept(this),
            operator = expression.operator,
            right = currentExpression.right.accept(this),
        )
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        return ParenExpression(expression.argument.accept(this))
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        return UnaryExpression(operator = expression.operator, argument = expression.argument.accept(this))
    }

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }
}
