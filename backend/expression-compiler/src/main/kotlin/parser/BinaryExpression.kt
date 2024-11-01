package org.example.parser

import org.example.visitors.Visitor

class BinaryExpression(val right: Expression, var left: Expression, val operator: String) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitBinaryExpression(this)
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
