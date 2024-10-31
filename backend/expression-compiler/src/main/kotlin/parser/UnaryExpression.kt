package org.example.parser

import org.example.parser.visitors.Visitor

class UnaryExpression(
    val operator: String,
    val argument: Expression, // TODO: rename to operand
) : Expression() {
    override fun accept(visitor: Visitor): Expression {
        return visitor.visitUnaryExpression(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is UnaryExpression) {
            return this.operator == other.operator && this.argument == other.argument
        }

        return false
    }

    override fun hashCode(): Int {
        var result = operator.hashCode()
        result = 31 * result + argument.hashCode()
        return result
    }

    override fun toString(): String {
        return "UnaryExpression { o = $operator, a = $argument }"
    }
}
