package org.example.parser.visitors

import org.example.parser.*

class ConstantFoldingVisitor : Visitor {
    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        val operator = expression.operator
        val argument = expression.argument // TODO: test unary paren exp

        if (argument is NumberLiteralExpression) {
            val value = argument.value
            return NumberLiteralExpression(value = "$operator$value")
        }

        return expression
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        return expression.expression.accept(this)
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val operator = expression.operator

        if (left is NumberLiteralExpression && right is NumberLiteralExpression) {
            val evaluatedResult = when (operator) {
                "+" -> left.value.toInt() + right.value.toInt() // TODO: handle floats
                "-" -> left.value.toInt() - right.value.toInt()
                "*" -> left.value.toInt() * right.value.toInt()
                "/" -> left.value.toInt() / right.value.toInt()
                else -> throw Exception("Unsupported operator $operator")
            }

            return NumberLiteralExpression(value = evaluatedResult.toString())
        }

        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        TODO("Not yet implemented")
    }
}
