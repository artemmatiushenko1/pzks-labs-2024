package org.example.visitors

import org.example.parser.*

interface Visitor {
    fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression
    fun visitIdentifierExpression(expression: IdentifierExpression): Expression
    fun visitUnaryExpression(expression: UnaryExpression): Expression
    fun visitParenExpression(expression: ParenExpression): Expression
    fun visitBinaryExpression(expression: BinaryExpression): Expression
}
