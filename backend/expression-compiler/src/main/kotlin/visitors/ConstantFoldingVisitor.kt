package org.example.visitors

import org.example.parser.*
import sun.nio.ch.Net.accept
import kotlin.math.absoluteValue

class ConstantFoldingVisitor : Visitor {
    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        val operator = expression.operator
        val argument = expression.argument.accept(this)
        // TODO: we also can try to open parens like -(-4)

        return UnaryExpression(operator = operator, argument = argument)
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        val foldResult = expression.argument.accept(this)

        if (foldResult is NumberLiteralExpression) {
            return foldResult
        }

        return ParenExpression(argument = foldResult)
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
            throw Exception("Division by zero is forbidden!")
        }

        return binaryExpression
    }

    private fun getNumberLiteralOrUnaryNumberLiteralValue(expression: Expression): String {
        if (expression is NumberLiteralExpression) {
            return expression.value
        }

        if (expression is UnaryExpression && expression.argument is NumberLiteralExpression) {
            return "${expression.operator}${expression.argument.value}"
        }

        throw IllegalArgumentException("Expression should be either NumberLiteralExpression or UnaryExpression with NumberLiteralExpression argument.")
    }

    private fun isNumberOrUnaryNumberLiteral(expression: Expression): Boolean {
        return expression is NumberLiteralExpression || (expression is UnaryExpression && expression.argument is NumberLiteralExpression)
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val operator = expression.operator

        verifyDivisionByZero(BinaryExpression(right, left, operator))

        if (isNumberOrUnaryNumberLiteral(left) && isNumberOrUnaryNumberLiteral(right)) {
            val leftValue = getNumberLiteralOrUnaryNumberLiteralValue(left).toInt()
            val rightValue = getNumberLiteralOrUnaryNumberLiteralValue(right).toInt()

            val evaluatedResult = evaluateBinaryExpression(
                leftValue,
                rightValue,
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
