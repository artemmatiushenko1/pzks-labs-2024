package org.example.parser.visitors

import org.example.parser.*

//a+b+0  ->  a+b
//a+1*b -> a+b
//a+b/1 -> a+b
//a+b+c*0 -> a+b
//a+1+2+3+4 -> a+10
//

class ConstantFoldingVisitor : Visitor {
    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        val operator = expression.operator
        val argument = expression.argument.accept(this) // TODO: test unary paren exp

        if (argument is NumberLiteralExpression) {
            val value = argument.value
            return NumberLiteralExpression(value = "$operator$value")
        }

        return UnaryExpression(operator = operator, argument = argument)
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        val foldResult = expression.expression.accept(this)
        return ParenExpression(expression = foldResult)
    }

    private fun evaluateBinaryExpression(left: Int, right: Int, operator: String): Int {
        return when (operator) {
            "+" -> left + right // TODO: handle floats
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            else -> throw Exception("Unsupported operator $operator")
        }
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val operator = expression.operator

        if (left is NumberLiteralExpression && right is NumberLiteralExpression) {
            return NumberLiteralExpression(
                value = evaluateBinaryExpression(
                    left.value.toInt(),
                    right.value.toInt(),
                    operator
                ).toString()
            )
        }

        if (left is BinaryExpression && right is NumberLiteralExpression) {
            if (left.right is NumberLiteralExpression) {
                return BinaryExpression(
                    left = left.left,
                    operator = left.operator,
                    right = NumberLiteralExpression(
                        evaluateBinaryExpression(
                            left = left.right.value.toInt(),
                            right = right.value.toInt(),
                            operator = operator,
                        ).toString()
                    )
                )
            }
        }

        return BinaryExpression(left = left, right = right, operator = operator)
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }
}
