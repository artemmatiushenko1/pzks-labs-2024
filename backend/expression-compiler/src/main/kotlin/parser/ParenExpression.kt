package org.example.parser

class ParenExpression(val expression: Expression) : Expression() {
    override fun equals(other: Any?): Boolean {
        if (other is ParenExpression) {
            return this.expression == other.expression
        }

        return false
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }

    override fun toString(): String {
        return "ParenExpression { e = $expression }"
    }
}
