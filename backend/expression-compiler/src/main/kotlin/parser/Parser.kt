package org.example.parser

import org.example.lexicalAnalyzer.Token
import org.example.lexicalAnalyzer.TokenType

/*
  NOTES: 
  1. Generate an AST.
  2. Perform optimisations.
  3. Visitor pattern may be useful for doing optimisations.

  TODO:
  - refactor sequential divisions,
  - refactor sequential subtraction,
  - put zero in front of unary expression etc.
 */
class Parser(val tokens: List<Token>) {
    private val position = 0
    private val _tokens = tokens.toMutableList()

    private fun getCurrentToken(): Token? {
        return this._tokens.getOrNull(this.position)
    }

    private fun consume(tokenType: TokenType): Token {
        val tokenToRemove = this._tokens.getOrNull(0)

        if (tokenToRemove?.type != tokenType) {
            throw Exception("Expected $tokenType, but got ${tokenToRemove?.type}")
        }

        return this._tokens.removeFirst()
    }

    private fun parseTerm(): Expression? {
        val currentToken = this.getCurrentToken()

        val expression = when (currentToken?.type) {
            TokenType.NUMBER -> NumberLiteralExpression(value = this.consume(TokenType.NUMBER).lexeme)
            TokenType.IDENTIFIER -> IdentifierExpression(value = this.consume(TokenType.IDENTIFIER).lexeme)
            else -> null
        }

        return expression
    }

    private fun parseParenExpression(): Expression? {
        var parenExpression: Expression? = null

        while (this.getCurrentToken()?.type == TokenType.OPEN_PAREN) {
            this.consume(TokenType.OPEN_PAREN).lexeme
            val expression = this.parseAdditiveExpression()
            this.consume(TokenType.CLOSE_PAREN).lexeme

            parenExpression = ParenExpression(expression = expression ?: throw Exception("Unexpected token!"))
        }

        return parenExpression ?: this.parseTerm()
    }

    private fun parseUnaryExpression(): Expression? {
        val currentToken = this.getCurrentToken()

        val expression = when (currentToken?.type) {
            TokenType.ADDITIVE_OPERATOR -> UnaryExpression(
                operator = this.consume(TokenType.ADDITIVE_OPERATOR).lexeme,
                argument = this.parseParenExpression() ?: throw Exception("Unexpected token!")
            )

            else -> this.parseParenExpression()
        }

        return expression
    }

    private fun parseMultiplicativeExpression(): Expression? {
        var left = this.parseUnaryExpression() ?: return null

        while (this.getCurrentToken()?.type == TokenType.MULTIPLICATIVE_OPERATOR) {
            val operator = this.consume(TokenType.MULTIPLICATIVE_OPERATOR).lexeme
            val right = this.parseUnaryExpression()

            left = BinaryExpression(
                left = left,
                operator = operator,
                right = right ?: throw Exception("Missing second operand in binary expression!")
            )
        }

        return left
    }

    private fun parseAdditiveExpression(): Expression? {
        var left = this.parseMultiplicativeExpression() ?: return null

        while (this.getCurrentToken()?.type == TokenType.ADDITIVE_OPERATOR) {
            val operator = this.consume(TokenType.ADDITIVE_OPERATOR).lexeme

            this.getCurrentToken()?.let { currentToken ->
                require(currentToken.lexeme != operator) { "Unexpected token!" }
            }

            val right = this.parseMultiplicativeExpression()

            left = BinaryExpression(
                left = left,
                operator = operator,
                right = right ?: throw Exception("Missing second operand in binary expression!")
            )
        }

        return left
    }

    fun parse(): ExpressionStatement {
        val expression = this.parseAdditiveExpression()
        return ExpressionStatement(expression = expression)
    }
}
