package org.example.parser

class IdentifierExpression(val value: String): Expression() {
    override fun equals(other: Any?): Boolean {
        if (other is IdentifierExpression) {
            return this.value == other.value
        }

        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
