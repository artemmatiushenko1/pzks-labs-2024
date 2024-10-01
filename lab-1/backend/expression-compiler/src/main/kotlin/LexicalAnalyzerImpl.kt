package org.example

internal class LexicalAnalyzerImpl(override val expressionSource: String) : LexicalAnalyzer {
    private val matchers: List<TokenMatcher> = listOf(
        TokenMatcher(regex = Regex("^\\s"), tokenType = TokenType.WHITESPACE, skip = true),
        TokenMatcher(regex = Regex("^\\("), tokenType = TokenType.OPEN_PAREN),
        TokenMatcher(regex = Regex("^\\)"), tokenType = TokenType.CLOSE_PAREN),
        TokenMatcher(regex = Regex("^([0-9]*(?<=\\d)\\.)?[0-9]+"), tokenType = TokenType.NUMBER),
        TokenMatcher(regex = Regex("^[+\\-]"), tokenType = TokenType.ADDITIVE_OPERATOR),
        TokenMatcher(regex = Regex("^[/*]"), tokenType = TokenType.MULTIPLICATIVE_OPERATOR),
        TokenMatcher(regex = Regex("^_*[a-zA-Z_0-9]+"), tokenType = TokenType.IDENTIFIER),
    )

    private fun findMatchedToken(input: String, currentCursorPosition: Int): Pair<TokenMatcher, Token>? {
        for (matcher in this.matchers) {
            val matchedTokenLexeme = matcher.match(input)

            if (matchedTokenLexeme != null) {
                return matcher to Token(
                    type = matcher.tokenType,
                    lexeme = matchedTokenLexeme,
                    position = currentCursorPosition
                )
            }
        }

        return null
    }

    override fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        var position = 0

        while (position < expressionSource.length) {
            val restOfExpression = expressionSource.slice(position until expressionSource.length)

            val matchedTokenResult = this.findMatchedToken(restOfExpression, position)
                ?: throw LexicalError("Unknown token '${expressionSource[position]}' at position $position.", position)

            val (tokenMatcher, token) = matchedTokenResult

            if (tokenMatcher.skip) {
                position = position.inc()
            } else {
                tokens.add(token)
                position += token.lexeme.length
            }
        }

        return tokens
    }
}
