package org.example

internal class SyntaxAnalyzerImpl(private val tokens: List<Token>) : SyntaxAnalyzer {
    private fun validateStartToken(): SyntaxError? {
        val startToken = this.tokens.first()

        if (startToken.type in listOf(TokenType.CLOSE_PAREN, TokenType.MULTIPLICATIVE_OPERATOR)) {
            return SyntaxError(
                "Expression should start with one of the following [number, identifier, open_paren, additive_operator].",
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

    private fun validateGrammar(): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()

        var skipNextTokenValidation = false

        for (index in tokens.indices) {
//            if (tokens.indices.last == index) {
//                // TODO: call validateEndToken
//            }

            if (skipNextTokenValidation) {
                skipNextTokenValidation = false
                continue
            }

            val currentToken = this.tokens[index]

            val nextTokenIndex = index + 1
            val nextToken = this.tokens.getOrNull(nextTokenIndex) ?: continue

            when (currentToken.type) {
                TokenType.ADDITIVE_OPERATOR, TokenType.MULTIPLICATIVE_OPERATOR -> {
                    if (nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    ) {
                        errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, open_paren] after '${currentToken.lexeme}'.",
                                position = nextToken.position
                            )
                        )

                        skipNextTokenValidation = true
                    }
                }

                TokenType.OPEN_PAREN -> {
                    if (nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN,
                            TokenType.ADDITIVE_OPERATOR,
                        )
                    ) {
                        errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, open_paren, math_operator] after '${currentToken.lexeme}'.",
                                position = nextToken.position
                            )
                        )

                        skipNextTokenValidation = true
                    }
                }

                TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.CLOSE_PAREN -> {
                    if (nextToken.type !in listOf(
                            TokenType.CLOSE_PAREN,
                            TokenType.ADDITIVE_OPERATOR,
                            TokenType.MULTIPLICATIVE_OPERATOR
                        )
                    ) {
                        errors.add(
                            SyntaxError(
                                "Expecting one of the following [math_operator, close_paren] after '${currentToken.lexeme}'.",
                                position = nextToken.position
                            )
                        )

                        skipNextTokenValidation = true
                    }
                }

                else -> continue
            }
        }

        return errors
    }

    override fun analyze(): List<SyntaxError> {
        if (tokens.isEmpty()) return emptyList()

        val errors = mutableListOf<SyntaxError>()

        validateParenthesisMatch()?.let { errors.add(it) }
        validateStartToken()?.let { errors.add(it) }
        validateGrammar().let { errors.addAll(it) }
        // TODO: move validate end token to validateGrammar,
        //  so we can identify the end of expression automatically without a need to check it explicitly
        validateEndToken()?.let { errors.add(it) }

        return errors.toList()
    }
}
