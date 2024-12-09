package org.example

import org.example.parser.*
import org.example.visitors.Visitor

class ToTasksVisitor : Visitor {
    private val tasks = mutableListOf<Task>()
    private val expressionToTaskMap = mutableMapOf<Expression, Task>()

    private var currentId = 1

    override fun visitNumberLiteralExpression(expression: NumberLiteralExpression): Expression {
        return expression
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression): Expression {
        return expression
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Expression {
        return expression.argument.accept(this)
    }

    override fun visitParenExpression(expression: ParenExpression): Expression {
        return expression.argument.accept(this)
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Expression {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)

        val taskType = when (expression.operator) {
            TaskType.SUM.operator -> TaskType.SUM
            TaskType.SUBTRACTION.operator -> TaskType.SUBTRACTION
            TaskType.MULTIPLICATION.operator -> TaskType.MULTIPLICATION
            TaskType.DIVISION.operator -> TaskType.DIVISION
            else -> {
                throw IllegalArgumentException("Unknown operator!")
            }
        }

        val task = Task(
            id = currentId,
            type = taskType,
            dependencies = listOfNotNull(
                expressionToTaskMap[left],
                expressionToTaskMap[right]
            )
        )

        tasks.add(task)
        expressionToTaskMap[expression] = task

        currentId += 1

        return expression
    }

    fun getTasks(): List<Task> {
        return this.tasks
    }
}
