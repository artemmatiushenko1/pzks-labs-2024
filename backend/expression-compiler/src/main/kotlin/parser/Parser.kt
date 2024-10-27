package org.example.parser

import org.example.Token
import org.example.TokenType

/*
  NOTES: 
  1. Generate an AST.
  2. Perform optimisations.
  3. Visitor pattern may be useful for doing optimisations.

  enum class NodeType {
    EXPRESSION_STATEMENT,
    BINARY_EXPRESSION,
    PARENTHESISED_EXPRESSION,
    IDENTIFIER,
    NUMBER_LITERAL,
  }
 */
class Parser(val tokens: List<Token>) {
    private val position = 0
    private val _tokens = tokens.toMutableList()

    private fun getCurrentToken(): Token? {
        return this._tokens.getOrNull(this.position)
    }

    private fun consume(): Token {
        return this._tokens.removeFirst()
    }

    private fun parseSimpleExpression(): Expression? {
        val currentToken = this.getCurrentToken()

        val expression = when (currentToken?.type) {
            TokenType.NUMBER -> NumberLiteralExpression(value = this.consume().lexeme)
            TokenType.IDENTIFIER -> IdentifierExpression(value = this.consume().lexeme)
            else -> null
        }

        return expression
    }

    fun parse(): ExpressionStatement {
        val left = this.parseSimpleExpression() ?: return ExpressionStatement(expression = null)

        val currentToken = this.getCurrentToken()

        val expression = when (currentToken?.type) {
            TokenType.ADDITIVE_OPERATOR -> {
                val operator = this.consume().lexeme
                val right = this.parseSimpleExpression()

                BinaryExpression(
                    left = left,
                    operator = operator,
                    right = right ?: throw Exception("Missing second operand in binary expression!")
                )
            }

            else -> null
        }

        return ExpressionStatement(expression = expression ?: left)
    }
}
