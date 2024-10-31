package org.example.parser

import org.example.parser.visitors.Visitor

class ParenExpression(val expression: Expression) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitParenExpression(this)
    }

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
