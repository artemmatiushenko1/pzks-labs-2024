package org.example.visitors

import org.example.TreeNode
import org.example.parser.*

class ToSerializableTreeVisitor : Visitor {
    private val tree = TreeNode(value = null)
    private var currentParent = tree

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        val numberLiteralNode = TreeNode(value = expression.value)
        currentParent.addNode(numberLiteralNode)

        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        val identifierNode = TreeNode(value = expression.value)
        currentParent.addNode(identifierNode)

        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        val unaryNode = TreeNode(value = expression.operator)
        val previousParent = currentParent

        currentParent.addNode(unaryNode)
        currentParent = unaryNode

        currentParent.addNode(TreeNode(value = "0")) // TODO: move this step to transformer, don't do this with unary number exp
        expression.argument.accept(this)

        currentParent = previousParent

        return expression
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        expression.expression.accept(this)

        return expression
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val binaryNode = TreeNode(value = expression.operator)
        val previousParent = currentParent
        currentParent.addNode(binaryNode)
        currentParent = binaryNode

        expression.left.accept(this)
        expression.right.accept(this)

        currentParent = previousParent

        return expression
    }

    fun getTree(): TreeNode {
        return this.tree
    }
}
