package org.example

/**
 * - помилки у кінці виразу (наприклад, вираз не може закінчуватись будь-
 * якою алгебраїчною операцією);
 * - помилки в середині виразу (подвійні операції, відсутність операцій
 * перед або між дужками, операції* або / після відкритої дужки тощо);
 */

class SyntaxAnalyzerImpl(private val tokens: List<Token>) : SyntaxAnalyzer {
    private val errors: MutableList<SyntaxError> = mutableListOf()

    private fun validateStartToken(): SyntaxError? {
        val startToken = this.tokens.first()
        val isMulOrDivOperator = startToken.type == TokenType.MATH_OPERATOR && startToken.lexeme in listOf("/", "*")

        if (startToken.type == TokenType.CLOSE_PAREN || isMulOrDivOperator) {
            return SyntaxError(
                "Expression should start with one of the following [number, identifier, open_paren].",
                position = 0
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
        validateStartToken()?.let {
            this.errors.add(it)
        }

        validateParenthesisMatch()?.let {
            this.errors.add(it)
        }

        var noValidateTokenPosition: Int? = null

        for (index in tokens.indices) {
            if (noValidateTokenPosition == index) continue
            noValidateTokenPosition = null

            val currentToken = this.tokens[index]

            val nextTokenIndex = index + 1
            val nextToken = this.tokens.getOrNull(nextTokenIndex)

            val previousTokenIndex = index - 1
            val previousToken = tokens.getOrNull(previousTokenIndex)

            when (currentToken.type) {
                TokenType.MATH_OPERATOR -> {
                    if ((previousToken == null && currentToken.lexeme in listOf(
                            "*",
                            "/"
                        )) || previousToken != null && previousToken.type !in listOf(
                            TokenType.NUMBER,
                            TokenType.IDENTIFIER,
                            TokenType.CLOSE_PAREN
                        )
                    ) {
                        val position = previousToken?.position ?: previousTokenIndex

                        this.errors.add(
                            SyntaxError(
                                "Expecting '${currentToken.lexeme}' to be preceded by one of the following [number, identifier, close_paren] at position $position.",
                                position = position
                            )
                        )
                    }

                    if (nextToken == null || nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    ) {
                        val position = nextToken?.position ?: nextTokenIndex

                        this.errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, open_paren] after '${currentToken.lexeme}' at position $position.",
                                position = position
                            )
                        )

                        noValidateTokenPosition = nextTokenIndex
                    }
                }

                TokenType.OPEN_PAREN -> {
                    if (nextToken != null && nextToken.type == TokenType.CLOSE_PAREN) {
                        this.errors.add(SyntaxError("Expecting an expression.", position = nextToken.position))
                    }
                }

                else -> continue
            }
        }

        return errors.toList()
    }
}
