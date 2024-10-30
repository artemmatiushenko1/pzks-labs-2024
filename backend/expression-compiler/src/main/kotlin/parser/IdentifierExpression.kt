package org.example.parser

import org.example.parser.visitors.Visitor

class IdentifierExpression(val value: String) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitIdentifierExpression(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is IdentifierExpression) {
            return this.value == other.value
        }

        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "IdentifierExpression { v = $value }"
    }
}
