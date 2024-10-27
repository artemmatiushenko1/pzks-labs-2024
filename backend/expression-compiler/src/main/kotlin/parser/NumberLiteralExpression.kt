package org.example.parser

class NumberLiteralExpression(val value: String): Expression() {
    override fun equals(other: Any?): Boolean {
        if (other is NumberLiteralExpression) {
            return this.value == other.value
        }

        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
