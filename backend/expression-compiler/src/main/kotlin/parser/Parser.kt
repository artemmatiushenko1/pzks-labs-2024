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

    private fun consume(): Token {
       return this.tokens.toMutableList().removeFirst()
    }

    fun parse(): ExpressionStatement {
        val expressionStatement = ExpressionStatement(expression = null)

        val currentToken = tokens[position]

        val expression = when (currentToken.type){
            TokenType.NUMBER -> NumberLiteralExpression(value = this.consume().lexeme)
            TokenType.IDENTIFIER -> IdentifierExpression(value = this.consume().lexeme)
            else -> throw Exception("Unknown token!")
        }

        expressionStatement.expression = expression

        return expressionStatement
    }
}
