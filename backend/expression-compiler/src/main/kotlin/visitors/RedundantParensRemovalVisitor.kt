package org.example.visitors

import org.example.parser.*

class RedundantParensRemovalVisitor : Visitor {
    override fun visitParenExpression(expression: ParenExpression): Expression {
        val argument = expression.argument

        if (argument is NumberLiteralExpression || argument is IdentifierExpression) {
            return argument
        }

        return if (argument is ParenExpression) {
            argument.accept(this)
        } else {
            expression
        }
    }

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        return UnaryExpression(operator = expression.operator, argument = expression.argument.accept(this))
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        return BinaryExpression(
            left = expression.left.accept(this),
            operator = expression.operator,
            right = expression.right.accept(this)
        )
    }
}
