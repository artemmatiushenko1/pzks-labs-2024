package org.example

/**
 * - помилки на початку арифметичного виразу (наприклад, вираз не може
 * починатись із закритої дужки, алгебраїчних операцій * та /);
 * - помилки, пов’язані з неправильним написанням імен змінних, констант
 * та при необхідності функцій;
 * - помилки у кінці виразу (наприклад, вираз не може закінчуватись будь-
 * якою алгебраїчною операцією);
 * - помилки в середині виразу (подвійні операції, відсутність операцій
 * перед або між дужками, операції* або / після відкритої дужки тощо);
 * - помилки, пов’язані з використанням дужок ( нерівна кількість відкритих
 * та закритих дужок, неправильний порядок дужок, пусті дужки).
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

    override fun analyze(): List<SyntaxError> {
        validateStartToken()?.let {
            this.errors.add(it)
        }

        var noValidateTokenPosition: Int? = null

        val iterationRange = tokens.indices.drop(1)

        for (index in iterationRange) {
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

                TokenType.IDENTIFIER -> {

                }

                TokenType.OPEN_PAREN -> {

                }

                TokenType.CLOSE_PAREN -> {}
                TokenType.NUMBER -> {}
            }
        }

        return errors.toList()
    }
}
