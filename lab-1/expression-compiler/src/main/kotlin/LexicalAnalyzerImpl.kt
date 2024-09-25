package org.example

class LexicalAnalyzerImpl(override val expressionSource: String) : LexicalAnalyzer {
    override val tokens: MutableList<Token> = mutableListOf()

    override fun tokenize(): List<Token> {
        val identifierRegex = Regex("^_*[a-zA-Z_0-9]+")
        val mathOperatorRegex = Regex("^[+\\-/*]")
        val whiteSpaceRegex = Regex("^\\s")
        val openParenthesisRegex = Regex("^\\(")
        val closeParenthesisRegex = Regex("^\\)")
        val numberRegex = Regex("^([0-9]*[.])?[0-9]+")

        var position = 0

        while (position < expressionSource.length) {
            val restOfExpression = expressionSource.slice(position until expressionSource.length)

            val whiteSpaceMatch = whiteSpaceRegex.find(restOfExpression)
            if (whiteSpaceMatch != null) {
                position = position.inc()
                continue
            }

            val openParenthesisMatch = openParenthesisRegex.find(restOfExpression)
            if (openParenthesisMatch != null) {
                this.tokens.add(Token(type = TokenType.OPEN_PAREN, lexeme = openParenthesisMatch.value))
                position += openParenthesisMatch.value.length
                continue
            }

            val closeParenthesisMatch = closeParenthesisRegex.find(restOfExpression)
            if (closeParenthesisMatch != null) {
                this.tokens.add(Token(type = TokenType.CLOSE_PAREN, lexeme = closeParenthesisMatch.value))
                position += closeParenthesisMatch.value.length
                continue
            }

            val numberMatch = numberRegex.find(restOfExpression)
            if (numberMatch != null) {
                this.tokens.add(Token(type = TokenType.NUMBER, lexeme = numberMatch.value))
                position += numberMatch.value.length
                continue
            }

            val mathOperatorMatch = mathOperatorRegex.find(restOfExpression)
            if (mathOperatorMatch != null) {
                this.tokens.add(Token(type = TokenType.MATH_OPERATOR, lexeme = mathOperatorMatch.value))
                position += mathOperatorMatch.value.length
                continue
            }

            val identifierMatch = identifierRegex.find(restOfExpression)
            if (identifierMatch != null) {
                this.tokens.add(Token(type = TokenType.IDENTIFIER, lexeme = identifierMatch.value))
                position += identifierMatch.value.length
                continue
            }

            throw LexicalError("Unknown token '${expressionSource[position]}' at position $position.", position)
        }

        return this.tokens
    }

}
