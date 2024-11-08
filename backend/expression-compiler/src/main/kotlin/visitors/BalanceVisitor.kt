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

    private fun rotateRight(expression: BinaryExpression): BinaryExpression {
        if (expression.left !is BinaryExpression) return expression

        val leftSubExpression = expression.left
        val rightOfLeftSubExpression = leftSubExpression.right

        if (
            (expression.isAddition() && leftSubExpression.isAddition()) ||
            (expression.isMultiplication() && leftSubExpression.isMultiplication())
        ) {
            return BinaryExpression(
                left = leftSubExpression.left,
                operator = leftSubExpression.operator,
                right = BinaryExpression(
                    left = rightOfLeftSubExpression,
                    operator = leftSubExpression.operator,
                    right = expression.right
                )
            )
        }

        return expression
    }

    private fun rotateLeft(expression: BinaryExpression): BinaryExpression {
        if (expression.right !is BinaryExpression) return expression

        val rightSubExpression = expression.right
        val leftOfRightSubExpression = rightSubExpression.left

        if (
            (expression.isAddition() && expression.right.isAddition()) ||
            (expression.isMultiplication() && expression.right.isMultiplication())
        ) {
            return BinaryExpression(
                left = BinaryExpression(
                    left = expression.left,
                    operator = expression.operator,
                    right = leftOfRightSubExpression
                ),
                operator = expression.operator,
                right = rightSubExpression.right
            )
        }

        return expression
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
