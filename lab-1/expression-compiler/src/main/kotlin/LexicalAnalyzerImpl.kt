package org.example

class LexicalAnalyzerImpl(override val expressionSource: String) : LexicalAnalyzer {
    override val tokens: MutableList<Token> = mutableListOf()

    override fun tokenize(): List<Token> {
        val integerRegex = Regex("^\\d+")
        val identifierRegex = Regex("^_*[a-zA-Z_]+")
        val mathOperatorRegex = Regex("[+-/*]")

        var cursor = 0

        while (cursor < expressionSource.length) {
            val restOfExpression = expressionSource.slice(cursor until expressionSource.length)

            val integerMatch = integerRegex.find(restOfExpression)
            if (integerMatch != null) {
                this.tokens.add(Token(type = TokenType.INTEGER, lexeme = integerMatch.value))
                cursor += integerMatch.value.length
                continue
            }

            val mathOperatorMatch = mathOperatorRegex.find(restOfExpression)
            if (mathOperatorMatch != null) {
                this.tokens.add(Token(type = TokenType.MATH_OPERATOR, lexeme = mathOperatorMatch.value))
                cursor += mathOperatorMatch.value.length
                continue
            }

            val identifierMatch = identifierRegex.find(restOfExpression)
            if (identifierMatch != null) {
                this.tokens.add(Token(type = TokenType.IDENTIFIER, lexeme = identifierMatch.value))
                cursor += identifierMatch.value.length
                continue
            }

            cursor = cursor.inc()
        }

        return this.tokens
    }

}
