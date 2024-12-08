package org.example

import org.example.parser.*
import org.example.visitors.Visitor

class ToInstructionsVisitor : Visitor {
    private val instructions = mutableListOf<Instruction>()
    private val expressionToInstructionMap = mutableMapOf<Expression, Instruction>()

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

        val instructionType = when (expression.operator) {
            InstructionType.SUM.operator -> InstructionType.SUM
            InstructionType.SUBTRACTION.operator -> InstructionType.SUBTRACTION
            InstructionType.MULTIPLICATION.operator -> InstructionType.MULTIPLICATION
            InstructionType.DIVISION.operator -> InstructionType.DIVISION
            else -> {
                throw IllegalArgumentException("Unknown operator!")
            }
        }

        val instruction = Instruction(
            id = currentId,
            type = instructionType,
            dependencies = listOfNotNull(
                expressionToInstructionMap[left],
                expressionToInstructionMap[right]
            )
        )

        instructions.add(instruction)
        expressionToInstructionMap[expression] = instruction

        currentId += 1

        return expression
    }

    fun getInstructions(): List<Instruction> {
        return this.instructions
    }
}
