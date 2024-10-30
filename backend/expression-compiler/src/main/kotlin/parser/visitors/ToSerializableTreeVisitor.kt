package org.example.parser.visitors

import org.example.TreeNode
import org.example.parser.*

class ToSerializableTreeVisitor : Visitor {
    private val tree = TreeNode(value = null)
    private var currentParent = tree

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression) {
        val numberLiteralNode = TreeNode(value = expression.value)
        currentParent.addNode(numberLiteralNode)
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression) {
        val identifierNode = TreeNode(value = expression.value)
        currentParent.addNode(identifierNode)
    }

    override fun visitUnaryExpression(expression: UnaryExpression) {
        val unaryNode = TreeNode(value = expression.operator)
        val previousParent = currentParent

        currentParent.addNode(unaryNode)
        currentParent = unaryNode

        currentParent.addNode(TreeNode(value = "0")) // TODO: move this step to transformer
        expression.argument.accept(this)

        currentParent = previousParent
    }

    override fun visitParenExpression(expression: ParenExpression) {
        expression.expression.accept(this)
    }

    override fun visitBinaryExpression(expression: BinaryExpression) {
        val binaryNode = TreeNode(value = expression.operator)
        val previousParent = currentParent
        currentParent.addNode(binaryNode)
        currentParent = binaryNode

        expression.left.accept(this)
        expression.right.accept(this)

        currentParent = previousParent
    }

    fun getTree(): TreeNode {
        return this.tree
    }
}
