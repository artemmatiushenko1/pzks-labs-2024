package org.example.parser

class ExpressionStatement(var expression: Expression?) {

    override fun equals(other: Any?): Boolean {
        if (other is ExpressionStatement) {
            return this.expression == other.expression
        }

        return false
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }
}
