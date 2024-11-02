package org.example.parser

import org.example.visitors.Visitor

class ParenExpression(val argument: Expression) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitParenExpression(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is ParenExpression) {
            return this.argument == other.argument
        }

        return false
    }

    override fun hashCode(): Int {
        return argument.hashCode()
    }

    override fun toString(): String {
        return "ParenExpression { a = $argument }"
    }
}
