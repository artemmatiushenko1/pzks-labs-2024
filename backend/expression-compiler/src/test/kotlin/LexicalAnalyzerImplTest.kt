import net.oddpoet.expect.expect
import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.lexicalAnalyzer.LexicalError
import org.example.lexicalAnalyzer.Token
import org.example.lexicalAnalyzer.TokenType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test

class LexicalAnalyzerImplTest {
    companion object {
        @JvmStatic
        fun provideSingleMathematicalOperatorExpressions(): List<Triple<String, String, TokenType>> {
            return listOf(
                Triple("+", "+", TokenType.ADDITIVE_OPERATOR),
                Triple("-", "-", TokenType.ADDITIVE_OPERATOR),
                Triple("/", "/", TokenType.MULTIPLICATIVE_OPERATOR),
                Triple("*", "*", TokenType.MULTIPLICATIVE_OPERATOR)
            )
        }

        @JvmStatic
        fun provideIdentifiersWithUnderscore(): List<Array<Any>> { // TODO: fix any
            return listOf(
                arrayOf("_variable", "_variable"),
                arrayOf("__variable", "__variable"),
                arrayOf("vari_able", "vari_able"),
                arrayOf("vari___able", "vari___able"),
                arrayOf("_vari__able__", "_vari__able__"),
                arrayOf("f1", "f1"),
                arrayOf("abc35", "abc35"),
                arrayOf("var345end5", "var345end5"),
            )
        }

        @JvmStatic
        fun provideSimpleExpressionsWithoutBrackets(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    "2+3",
                    listOf(
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 0),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 1),
                        Token(type = TokenType.NUMBER, lexeme = "3", position = 2)
                    )
                ),
                arrayOf(
                    "0*345-8+55/2",
                    listOf(
                        Token(type = TokenType.NUMBER, lexeme = "0", position = 0),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "*", position = 1),
                        Token(type = TokenType.NUMBER, lexeme = "345", position = 2),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 5),
                        Token(type = TokenType.NUMBER, lexeme = "8", position = 6),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 7),
                        Token(type = TokenType.NUMBER, lexeme = "55", position = 8),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "/", position = 10),
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 11),
                    )
                ),
                arrayOf(
                    "variAble_-2-+9873*vari_able", listOf(
                        Token(type = TokenType.IDENTIFIER, lexeme = "variAble_", position = 0),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 9),
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 10),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 11),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 12),
                        Token(type = TokenType.NUMBER, lexeme = "9873", position = 13),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "*", position = 17),
                        Token(type = TokenType.IDENTIFIER, lexeme = "vari_able", position = 18),
                    )
                )
            )
        }

        @JvmStatic
        fun provideExpressionsWithUnknownTokens(): List<Array<Any>> {
            return listOf(
                arrayOf("1+3*#9", "Unknown token '#'"),
                arrayOf("3/3*3?2$", "Unknown token '?'"),
                arrayOf(" 5 /variable+6&89", "Unknown token '&'")
            )
        }

        @JvmStatic
        fun provideExpressionsWithParenthesis(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    "2*(1+1)",
                    listOf(
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 0),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "*", position = 1),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 2),
                        Token(type = TokenType.NUMBER, lexeme = "1", position = 3),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 4),
                        Token(type = TokenType.NUMBER, lexeme = "1", position = 5),
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 6),
                    )
                ),
                arrayOf(
                    ")((246*(1+1)/(4-2))",
                    listOf(
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 0),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 1),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 2),
                        Token(type = TokenType.NUMBER, lexeme = "246", position = 3),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "*", position = 6),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 7),
                        Token(type = TokenType.NUMBER, lexeme = "1", position = 8),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 9),
                        Token(type = TokenType.NUMBER, lexeme = "1", position = 10),
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 11),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "/", position = 12),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 13),
                        Token(type = TokenType.NUMBER, lexeme = "4", position = 14),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 15),
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 16),
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 17),
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 18),
                    )
                ),
            )
        }

        @JvmStatic
        fun provideExpressionsWithFloatNumbers(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    "-1.2*2+(0.55+1.99999)",
                    listOf(
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 0),
                        Token(type = TokenType.NUMBER, lexeme = "1.2", position = 1),
                        Token(type = TokenType.MULTIPLICATIVE_OPERATOR, lexeme = "*", position = 4),
                        Token(type = TokenType.NUMBER, lexeme = "2", position = 5),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 6),
                        Token(type = TokenType.OPEN_PAREN, lexeme = "(", position = 7),
                        Token(type = TokenType.NUMBER, lexeme = "0.55", position = 8),
                        Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 12),
                        Token(type = TokenType.NUMBER, lexeme = "1.99999", position = 13),
                        Token(type = TokenType.CLOSE_PAREN, lexeme = ")", position = 20),
                    )
                )
            )
        }
    }

    @Test
    fun `tokenize returns empty list when expression source is empty`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "")
        lexicalAnalyzer.tokenize().should.equal(emptyList<Token>())
    }

    @Test
    fun `tokenize returns a list with single digit NUMBER token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "1")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.NUMBER, lexeme = "1", position = 0)))
    }

    @Test
    fun `tokenize returns a list with multiple digit number token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "1345")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.NUMBER, lexeme = "1345", position = 0)))
    }

    @Test
    fun `tokenize returns a list with single char identifier token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "a")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = "a", position = 0)))
    }

    @Test
    fun `tokenize returns a list with multiple char identifier token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "variable")
        lexicalAnalyzer.tokenize().should.equal(
            listOf(
                Token(
                    type = TokenType.IDENTIFIER,
                    lexeme = "variable",
                    position = 0
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideIdentifiersWithUnderscore")
    fun `tokenize returns a list with identifier token that contains underscore`(
        expressionSource: String,
        expectedLexeme: String
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(
            listOf(
                Token(
                    type = TokenType.IDENTIFIER,
                    lexeme = expectedLexeme,
                    position = 0
                )
            )
        )
    }

    @Test
    fun `tokenize returns a list with identifier token that contains uppercase letters`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "CONSTANT")
        lexicalAnalyzer.tokenize().should.equal(
            listOf(
                Token(
                    type = TokenType.IDENTIFIER,
                    lexeme = "CONSTANT",
                    position = 0
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideSingleMathematicalOperatorExpressions")
    fun `tokenize returns a list with mathematical operator token`(input: Triple<String, String, TokenType>) {
        val (expressionSource, expectedLexeme, tokenType) = input
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(
            listOf(
                Token(
                    type = tokenType,
                    lexeme = expectedLexeme,
                    position = 0
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideSimpleExpressionsWithoutBrackets")
    fun `tokenize returns correct tokens for a simple expression without brackets`(
        expressionSource: String,
        expectedTokens: List<Token>
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(expectedTokens)
    }

    @Test
    fun `tokenize skips whitespace tokens`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = " 2+4 -  123 ")
        lexicalAnalyzer.tokenize().should.equal(
            listOf(
                Token(type = TokenType.NUMBER, lexeme = "2", position = 1),
                Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "+", position = 2),
                Token(type = TokenType.NUMBER, lexeme = "4", position = 3),
                Token(type = TokenType.ADDITIVE_OPERATOR, lexeme = "-", position = 5),
                Token(type = TokenType.NUMBER, lexeme = "123", position = 8)
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideExpressionsWithUnknownTokens")
    fun `tokenize throws LexicalError exception when unknown token is met`(
        expressionSource: String,
        expectedErrorMessage: String
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        expect {
            lexicalAnalyzer.tokenize()
        }.throws(LexicalError::class) { it.message.should.equal(expectedErrorMessage) }
    }

    @ParameterizedTest
    @MethodSource("provideExpressionsWithParenthesis")
    fun `tokenize returns correct set of tokens for expression with parenthesis`(
        expressionSource: String,
        expectedTokens: List<Token>
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(expectedTokens)
    }

    @ParameterizedTest
    @MethodSource("provideExpressionsWithFloatNumbers")
    fun `tokenize returns correct set of tokens for expression with float numbers`(
        expressionSource: String,
        expectedTokens: List<Token>
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(expectedTokens)
    }
}
