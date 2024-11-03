package org.example.visitors

import org.example.parser.*

class AlgebraicSimplificationVisitor : Visitor {
    private val simplifiers = listOf(
        AlgebraicSimplificationVisitor::simplifyMultiplicationByZero,
        AlgebraicSimplificationVisitor::simplifyZeroDividedBy,
        AlgebraicSimplificationVisitor::simplifyDivisionByOne,
        AlgebraicSimplificationVisitor::simplifyMultiplicationByOne,
        AlgebraicSimplificationVisitor::simplifyAdditiveWithZero,
        AlgebraicSimplificationVisitor::simplifySelfDivision,
        AlgebraicSimplificationVisitor::transformSequentialDivision,
        AlgebraicSimplificationVisitor::transformSequentialSubtraction,
    )

    override fun visitParenExpression(expression: ParenExpression): Expression {
        val simplifiedExpression = expression.argument.accept(this)

        if (simplifiedExpression is NumberLiteralExpression && simplifiedExpression.value == "0") {
            return simplifiedExpression
        }

        return ParenExpression(argument = simplifiedExpression)
    }

    private fun isMultiplicationByZero(left: Expression, right: Expression, operator: String): Boolean {
        return operator == "*" &&
                ((left is NumberLiteralExpression && left.value == "0") ||
                        (right is NumberLiteralExpression && right.value == "0"))
    }

    private fun isZeroDividedBy(left: Expression, operator: String): Boolean {
        return operator == "/" && (left is NumberLiteralExpression && left.value == "0")
    }

    private fun isDivisionByOne(right: Expression, operator: String): Boolean {
        return operator == "/" && (right is NumberLiteralExpression && right.value == "1")
    }

    private fun isMultiplicationByOne(left: Expression, right: Expression, operator: String): Expression? {
        if (operator != "*") return null

        if (left is NumberLiteralExpression && left.value == "1") {
            return right
        }

        if (right is NumberLiteralExpression && right.value == "1") {
            return left
        }

        return null
    }

    private fun isAdditiveWithZero(left: Expression, right: Expression, operator: String): Expression? {
        if (operator !in listOf("+", "-")) return null

        val isLeftZero = left is NumberLiteralExpression && left.value == "0"
        val isRightZero = right is NumberLiteralExpression && right.value == "0"

        return when {
            operator == "+" && (isLeftZero || isRightZero) -> if (isLeftZero) right else left
            operator == "-" && isRightZero -> left
            else -> null
        }
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

    private fun simplifyZeroDividedBy(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val left = binaryExpression.left
        val right = binaryExpression.right

        if (isZeroDividedBy(left, operator)) {
            return NumberLiteralExpression("0")
        }

        return BinaryExpression(left = left, right = right, operator = operator)
    }

    private fun simplifyDivisionByOne(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val left = binaryExpression.left
        val right = binaryExpression.right

        return left.takeIf { isDivisionByOne(right, operator) } ?: binaryExpression
    }

    private fun simplifyMultiplicationByOne(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val left = binaryExpression.left
        val right = binaryExpression.right

        return isMultiplicationByOne(left, right, operator) ?: binaryExpression
    }

    private fun simplifyAdditiveWithZero(binaryExpression: BinaryExpression): Expression {
        val operator = binaryExpression.operator
        val left = binaryExpression.left
        val right = binaryExpression.right

        return isAdditiveWithZero(left, right, operator) ?: binaryExpression
    }

    private fun simplifySelfDivision(binaryExpression: BinaryExpression): Expression {
        if (binaryExpression.left == binaryExpression.right && binaryExpression.operator == "/") {
            return NumberLiteralExpression("1")
        }

        return binaryExpression
    }

    private fun transformSequentialDivision(binaryExpression: BinaryExpression): Expression {
        if (binaryExpression.operator == "/" && (binaryExpression.left is BinaryExpression && binaryExpression.left.operator == "/")) {
            return BinaryExpression(
                left = binaryExpression.left.left,
                operator = binaryExpression.operator,
                right = BinaryExpression(
                    left = binaryExpression.left.right,
                    operator = "*",
                    right = binaryExpression.right
                )
            )
        }

        return binaryExpression
    }

    private fun transformSequentialSubtraction(binaryExpression: BinaryExpression): Expression {
        if (binaryExpression.operator == "-" && (binaryExpression.left is BinaryExpression && binaryExpression.left.operator == "-")) {
            return BinaryExpression(
                left = binaryExpression.left.left,
                operator = binaryExpression.operator,
                right = BinaryExpression(
                    left = binaryExpression.left.right,
                    operator = "+",
                    right = binaryExpression.right
                )
            )
        }

        return binaryExpression
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val operator = expression.operator
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)

        return simplifiers.fold(
            BinaryExpression(
                left = left,
                right = right,
                operator = operator
            ) as Expression
        ) { expr, simplify -> if (expr is BinaryExpression) simplify.invoke(this, expr) else expr }
    }

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        if (expression.argument is ParenExpression) {
            val parenArgument = expression.argument.argument

            if (parenArgument is UnaryExpression && (expression.operator == parenArgument.operator)) {
                return ParenExpression(argument = parenArgument.argument)
            }
        }

        return BinaryExpression(
            left = NumberLiteralExpression("0"),
            operator = expression.operator,
            right = expression.argument
        )
    }
}
