package org.example.parser

interface Visitor {
    fun visitNumberLiteralExpression(expression: NumberLiteralExpression)
    fun visitIdentifierExpression(expression: IdentifierExpression)
    fun visitUnaryExpression(expression: UnaryExpression)
    fun visitParenExpression(expression: ParenExpression)
    fun visitBinaryExpression(expression: BinaryExpression)
}
