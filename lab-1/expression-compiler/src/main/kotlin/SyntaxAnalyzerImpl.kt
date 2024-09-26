package org.example

class SyntaxAnalyzerImpl(private val tokens: List<Token>) : SyntaxAnalyzer {
    private val errors: MutableList<SyntaxError> = mutableListOf()

    override fun analyze(): List<SyntaxError> {
        for (index in this.tokens.indices) {
            val currentToken = this.tokens[index]

            val nextTokenPosition = index + 1
            val nextToken = this.tokens.getOrNull(nextTokenPosition)

            val previousTokenPosition = index - 1
            val previousToken = tokens.getOrNull(previousTokenPosition)

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
                        this.errors.add(
                            SyntaxError(
                                "Expecting '${currentToken.lexeme}' to be preceded by one of the following [number, identifier, close_paren] at position $previousTokenPosition.",
                                position = previousTokenPosition
                            )
                        )
                    }
                    
                    if (nextToken == null || nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    ) {
                        this.errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, open_paren] after '${currentToken.lexeme}' at position $nextTokenPosition.",
                                position = nextTokenPosition
                            )
                        )
                    }
                }

                TokenType.IDENTIFIER -> {}
                TokenType.OPEN_PAREN -> {}
                TokenType.CLOSE_PAREN -> {}
                TokenType.NUMBER -> {}
            }
        }

        return errors.toList()
    }
}
