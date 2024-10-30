package org.example.parser

import org.example.parser.visitors.Visitor

class NumberLiteralExpression(val value: String) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitNumberLiteralExpression(this)
    }

    fun isFloat(): Boolean {
        return this.value.toFloatOrNull() != null
    }

    fun isInt(): Boolean {
        return this.value.toIntOrNull() != null
    }

    override fun equals(other: Any?): Boolean {
        if (other is NumberLiteralExpression) {
            return this.value == other.value
        }

        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "NumberLiteralExpression { v = $value }"
    }
}
