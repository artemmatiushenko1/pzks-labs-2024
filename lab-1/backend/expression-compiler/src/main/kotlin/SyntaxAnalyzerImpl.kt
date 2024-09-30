package org.example

internal class SyntaxAnalyzerImpl(private val tokens: List<Token>) : SyntaxAnalyzer {
    private val errors: MutableList<SyntaxError> = mutableListOf()

    private fun validateStartToken(): SyntaxError? {
        val startToken = this.tokens.first()
        val isMulOrDivOperator = startToken.type == TokenType.MATH_OPERATOR && startToken.lexeme in listOf("/", "*")

        if (startToken.type == TokenType.CLOSE_PAREN || isMulOrDivOperator) {
            return SyntaxError(
                "Expression should start with one of the following [number, identifier, open_paren].",
                position = startToken.position
            )
        }

        return null
    }

    private fun validateEndToken(): SyntaxError? {
        val endToken = this.tokens.last()

        if (endToken.type !in listOf(TokenType.CLOSE_PAREN, TokenType.IDENTIFIER, TokenType.NUMBER)) {
            return SyntaxError(
                "Expression should end with one of the following [number, identifier, close_paren].",
                position = endToken.position
            )
        }

        return null
    }

    private fun validateParenthesisMatch(): SyntaxError? {
        var parenthesisCounter = 0
        val parenthesisTokens = tokens.filter { it.type in listOf(TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN) }

        for (token in parenthesisTokens) {
            parenthesisCounter = when (token.type) {
                TokenType.OPEN_PAREN -> parenthesisCounter.inc()
                TokenType.CLOSE_PAREN -> parenthesisCounter.dec()
                else -> throw IllegalArgumentException("Unexpected token '${token.lexeme}' of type ${token.type}.")
            }
        }

        if (parenthesisCounter != 0) {
            return SyntaxError("Parenthesis mismatch.", position = null)
        }

        return null
    }

    override fun analyze(): List<SyntaxError> {
        if(tokens.isEmpty()) return this.errors

        validateParenthesisMatch()?.let {
            this.errors.add(it)
        }
        
        validateStartToken()?.let {
            this.errors.add(it)
        }

        validateEndToken()?.let {
            this.errors.add(it)
        }

        var noValidateTokenPosition: Int? = null

        for (index in tokens.indices) {
            if (noValidateTokenPosition == index) continue
            noValidateTokenPosition = null

            val currentToken = this.tokens[index]

            val nextTokenIndex = index + 1
            val nextToken = this.tokens.getOrNull(nextTokenIndex)

            when (currentToken.type) {
                TokenType.MATH_OPERATOR -> {
                    if (nextToken != null && nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    ) {
                        val position = nextToken.position

                        this.errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, open_paren] after '${currentToken.lexeme}'.",
                                position = position
                            )
                        )

                        noValidateTokenPosition = nextTokenIndex
                    }
                }

                TokenType.OPEN_PAREN -> {
                    if (nextToken != null && nextToken.type == TokenType.CLOSE_PAREN || nextToken == null) {
                        val position = nextToken?.position ?: nextTokenIndex
                        this.errors.add(SyntaxError("Expecting an expression.", position = position))
                    }
                }

                else -> continue
            }
        }

        return errors.toList()
    }
}
