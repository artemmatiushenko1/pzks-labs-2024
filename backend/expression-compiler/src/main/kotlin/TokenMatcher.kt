package org.example

internal class TokenMatcher(private val regex: Regex, val skip: Boolean = false, val tokenType: TokenType) {
    fun match(input: String): String? {
        return this.regex.find(input)?.value
    }
}
