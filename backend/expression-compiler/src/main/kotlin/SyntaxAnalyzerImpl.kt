package org.example

internal class SyntaxAnalyzerImpl(private val tokens: List<Token>) : SyntaxAnalyzer {
    private fun validateStartToken(startToken: Token): SyntaxError? {
        val expectedTokens =
            listOf(TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.OPEN_PAREN, TokenType.ADDITIVE_OPERATOR)

        if (startToken.type !in expectedTokens) {
            return SyntaxError(
                "Expression should start with one of the following [${expectedTokens.joinToString(", ")}].",
                position = startToken.position
            )
        }

        return null
    }

    private fun validateEndToken(endToken: Token): SyntaxError? {
        val expectedTokens = listOf(TokenType.CLOSE_PAREN, TokenType.IDENTIFIER, TokenType.NUMBER)

        if (endToken.type !in listOf(TokenType.CLOSE_PAREN, TokenType.IDENTIFIER, TokenType.NUMBER)) {
            return SyntaxError(
                "Expression should end with one of the following [${expectedTokens.joinToString(", ")}].",
                position = endToken.position
            )
        }

        return null
    }

    private fun validateParenthesisMatch(): SyntaxError? {
        val stack = mutableListOf<Token>()
        val parenthesisTokens = tokens.filter { it.type in listOf(TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN) }

        for (token in parenthesisTokens) {
            when (token.type) {
                TokenType.OPEN_PAREN -> {
                    stack.add(token)
                }

                TokenType.CLOSE_PAREN -> {
                    val lastToken = stack.removeLastOrNull()

                    if (lastToken == null || lastToken.type != TokenType.OPEN_PAREN) {
                        return SyntaxError("Parentheses mismatch.", position = null)
                    }
                }

                else -> throw IllegalArgumentException("Unexpected token '${token.lexeme}' of type ${token.type}.")
            }
        }

        if (stack.isNotEmpty()) {
            return SyntaxError("Parentheses mismatch.", position = null)
        }

        return null
    }

    private fun verifyNextTokenExpectation(
        currentToken: Token,
        nextToken: Token,
        expectedTokenTypes: List<TokenType>
    ): SyntaxError? {
        if (nextToken.type !in expectedTokenTypes) {
            return SyntaxError(
                "Expecting one of the following [${expectedTokenTypes.joinToString(", ")}] after '${currentToken.lexeme}'.",
                position = nextToken.position
            )
        }

        return null
    }

    private fun validateGrammar(): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()

        var skipNextTokenValidation = false

        for ((index, currentToken) in tokens.withIndex()) {
            if (tokens.indices.first == index) {
                this.validateStartToken(currentToken)?.let { errors.add(it) }
            }

            if (tokens.indices.last == index) {
                this.validateEndToken(currentToken)?.let { errors.add(it) }
            }

            if (skipNextTokenValidation) {
                skipNextTokenValidation = false
                continue
            }

            val nextTokenIndex = index.inc()
            val nextToken = this.tokens.getOrNull(nextTokenIndex) ?: continue

            val error = when (currentToken.type) {
                TokenType.ADDITIVE_OPERATOR, TokenType.MULTIPLICATIVE_OPERATOR -> {
                    verifyNextTokenExpectation(
                        currentToken = currentToken,
                        nextToken = nextToken,
                        expectedTokenTypes = listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    )
                }

                TokenType.OPEN_PAREN -> {
                    verifyNextTokenExpectation(
                        currentToken = currentToken,
                        nextToken = nextToken,
                        expectedTokenTypes = listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN,
                            TokenType.ADDITIVE_OPERATOR,
                        )
                    )
                }

                TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.CLOSE_PAREN -> {
                    verifyNextTokenExpectation(
                        currentToken = currentToken,
                        nextToken = nextToken,
                        expectedTokenTypes = listOf(
                            TokenType.CLOSE_PAREN,
                            TokenType.ADDITIVE_OPERATOR,
                            TokenType.MULTIPLICATIVE_OPERATOR
                        )
                    )
                }

                TokenType.WHITESPACE -> null
            }

            if (error != null) {
                errors.add(error)
                skipNextTokenValidation = true
            }
        }

        return errors
    }

    override fun analyze(): List<SyntaxError> {
        if (tokens.isEmpty()) return emptyList()

        val errors = mutableListOf<SyntaxError>()

        validateParenthesisMatch()?.let { errors.add(it) }
        validateGrammar().let { errors.addAll(it) }

        return errors.toList()
    }
}
