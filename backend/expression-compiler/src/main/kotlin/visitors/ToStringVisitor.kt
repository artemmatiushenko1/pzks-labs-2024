package org.example.visitors

import org.example.parser.*

class ToStringVisitor : Visitor {
    private var expressionString = ""

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        expressionString += expression.value
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        expressionString += expression.value
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        expressionString += expression.operator
        expression.argument.accept(this)

        return expression
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        expressionString += "("
        expression.argument.accept(this)
        expressionString += ")"

        return expression
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        expression.left.accept(this)
        expressionString += expression.operator
        expression.right.accept(this)

        return expression
    }

    fun getExpressionString(): String {
        return this.expressionString
    }
}
