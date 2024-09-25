package org.example

class TokenMatcher(private val regex: Regex, val skip: Boolean = false, val tokenType: TokenType) {
    fun match(input: String): String? {
        return this.regex.find(input)?.value
    }
}

class LexicalAnalyzerImpl(override val expressionSource: String) : LexicalAnalyzer {
    override val tokens: MutableList<Token> = mutableListOf()

    private val matchers: List<TokenMatcher> = listOf(
        TokenMatcher(regex = Regex("^\\s"), tokenType = TokenType.MATH_OPERATOR, skip = true),
        TokenMatcher(regex = Regex("^\\("), tokenType = TokenType.OPEN_PAREN),
        TokenMatcher(regex = Regex("^\\)"), tokenType = TokenType.CLOSE_PAREN),
        TokenMatcher(regex = Regex("^([0-9]*[.])?[0-9]+"), tokenType = TokenType.NUMBER),
        TokenMatcher(regex = Regex("^[+\\-/*]"), tokenType = TokenType.MATH_OPERATOR),
        TokenMatcher(regex = Regex("^_*[a-zA-Z_0-9]+"), tokenType = TokenType.IDENTIFIER),
    )

    private fun findMatchedToken(input: String): Pair<TokenMatcher, Token>? {
        for (matcher in this.matchers) {
            val matchedTokenLexeme = matcher.match(input)

            if (matchedTokenLexeme != null) {
                return matcher to Token(type = matcher.tokenType, lexeme = matchedTokenLexeme)
            }
        }

        return null
    }

    override fun tokenize(): List<Token> {
        var position = 0

        while (position < expressionSource.length) {
            val restOfExpression = expressionSource.slice(position until expressionSource.length)

            val matchedTokenResult = this.findMatchedToken(restOfExpression)
                ?: throw LexicalError("Unknown token '${expressionSource[position]}' at position $position.", position)

            val (tokenMatcher, token) = matchedTokenResult

            if (tokenMatcher.skip) {
                position = position.inc()
            } else {
                this.tokens.add(token)
                position += token.lexeme.length
            }
        }

        return this.tokens
    }
}
