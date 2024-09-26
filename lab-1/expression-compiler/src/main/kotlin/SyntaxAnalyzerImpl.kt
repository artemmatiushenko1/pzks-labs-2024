package org.example

class SyntaxAnalyzerImpl : SyntaxAnalyzer {
    override fun analyze(tokens: List<Token>): List<SyntaxError> {
        val errors: MutableList<SyntaxError> = mutableListOf()

        for (index in tokens.indices) {
            val currentToken = tokens[index]

            val nextTokenPosition = index + 1
            val nextToken = tokens.getOrNull(nextTokenPosition)

            when (currentToken.type) {
                TokenType.MATH_OPERATOR -> {
                    if (nextToken == null || nextToken.type !in listOf(
                            TokenType.IDENTIFIER,
                            TokenType.NUMBER,
                            TokenType.OPEN_PAREN
                        )
                    ) {
                        errors.add(
                            SyntaxError(
                                "Expecting one of the following [number, identifier, close_paren] after '${currentToken.lexeme}' at position $nextTokenPosition.",
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
