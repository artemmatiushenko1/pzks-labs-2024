package org.example.parser

class BinaryExpression(val right: Expression, val left: Expression, val operator: String) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.visitBinaryExpression(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is BinaryExpression) {
            return this.right == other.right && this.left == other.left && this.operator == other.operator
        }

        return false
    }

    override fun hashCode(): Int {
        var result = right.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + operator.hashCode()
        return result
    }

    override fun toString(): String {
        return "BinaryExpression { l = $left, op = $operator, r = $right}"
    }
}
