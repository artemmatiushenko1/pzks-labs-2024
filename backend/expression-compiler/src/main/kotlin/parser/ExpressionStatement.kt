package org.example.parser

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.visitors.ToSerializableTreeVisitor

class ExpressionStatement(var expression: Expression?) {

    override fun equals(other: Any?): Boolean {
        if (other is ExpressionStatement) {
            return this.expression == other.expression
        }

        return false
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }

    override fun toString(): String {
        return "ExpressionStatement -> $expression"
    }

    fun toSerializableTree(): String {
        val toSerializableTreeVisitor = ToSerializableTreeVisitor()
        this.expression?.accept(toSerializableTreeVisitor)
        return Json.encodeToString(toSerializableTreeVisitor.getTree())
    }
}
