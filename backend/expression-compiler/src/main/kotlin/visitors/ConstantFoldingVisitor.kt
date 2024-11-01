package org.example.visitors

import org.example.parser.*
import kotlin.math.absoluteValue
import kotlin.math.sign

//a+b+0  ->  a+b
//a+1*b -> a+b
//a+b/1 -> a+b

class ConstantFoldingVisitor : Visitor {
    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        val operator = expression.operator
        val argument = expression.argument.accept(this)

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

    private fun verifyDivisionByZero(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val right = binaryExpression.right

        if (operator == "/" && right is NumberLiteralExpression && right.value == "0") {
            throw IllegalArgumentException("Division by zero is forbidden!")
        }

        return binaryExpression
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val operator = expression.operator

        verifyDivisionByZero(BinaryExpression(right, left, operator))

        if (left is NumberLiteralExpression && right is NumberLiteralExpression) {
            val evaluatedResult = evaluateBinaryExpression(
                left.value.toInt(),
                right.value.toInt(),
                operator
            )

            val outputExpr = if (evaluatedResult < 0) {
                UnaryExpression(
                    operator = "-",
                    argument = NumberLiteralExpression(evaluatedResult.absoluteValue.toString())
                )
            } else {
                NumberLiteralExpression(evaluatedResult.toString())
            }

            return outputExpr
        }

        if (left is BinaryExpression && right is NumberLiteralExpression) {
            // TODO: create enum for math operators
            if (left.right is NumberLiteralExpression && left.operator !in listOf("*", "/")) {
                val evaluatedResult = evaluateBinaryExpression(
                    left = left.right.value.toInt().let {
                        if (left.operator == "-") {
                            it.unaryMinus()
                        } else {
                            it
                        }
                    },
                    right = right.value.toInt(),
                    operator = operator,
                )

                val outputExpr = NumberLiteralExpression(evaluatedResult.absoluteValue.toString())


                return BinaryExpression(
                    left = left.left,
                    operator = if (evaluatedResult < 0) "-" else "+",
                    right = outputExpr
                )
            }
        }

        return BinaryExpression(left = left, right = right, operator = operator)
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }
}
