import net.oddpoet.expect.expect
import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.LexicalError
import org.example.Token
import org.example.TokenType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test

// TARGET INPUT SOURCE: a+b*(c*cos(t-a*x)-d*sin(t+a*x)/(4.81*k-q*t))/(d*cos(t+a*y/f1(5.616*x-t))+c*sin(t-a*y*(u-v*i)))

class LexicalAnalyzerImplTest {
    companion object {
        @JvmStatic
        fun provideSingleMathematicalOperatorExpressions(): List<Array<Any>> { // todo: fix any
            return listOf(
                arrayOf("+", "+"),
                arrayOf("-", "-"),
                arrayOf("/", "/"),
                arrayOf("*", "*")
            )
        }

        @JvmStatic
        fun provideIdentifiersWithUnderscore(): List<Array<Any>> {
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
                        Token(type = TokenType.INTEGER, lexeme = "2"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "+"),
                        Token(type = TokenType.INTEGER, lexeme = "3")
                    )
                ),
                arrayOf(
                    "0*345-8+55/2",
                    listOf(
                        Token(type = TokenType.INTEGER, lexeme = "0"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "*"),
                        Token(type = TokenType.INTEGER, lexeme = "345"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "-"),
                        Token(type = TokenType.INTEGER, lexeme = "8"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "+"),
                        Token(type = TokenType.INTEGER, lexeme = "55"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "/"),
                        Token(type = TokenType.INTEGER, lexeme = "2"),
                    )
                ),
                arrayOf(
                    "variAble_-2-+9873*vari_able", listOf(
                        Token(type = TokenType.IDENTIFIER, lexeme = "variAble_"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "-"),
                        Token(type = TokenType.INTEGER, lexeme = "2"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "-"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "+"),
                        Token(type = TokenType.INTEGER, lexeme = "9873"),
                        Token(type = TokenType.MATH_OPERATOR, lexeme = "*"),
                        Token(type = TokenType.IDENTIFIER, lexeme = "vari_able"),
                    )
                )
            )
        }

        @JvmStatic
        fun provideExpressionsWithUnknownTokens(): List<Array<Any>> {
            return listOf(
                arrayOf("1+3*#9", "Unknown token '#' at position 4."),
                arrayOf("3/3*3?2$", "Unknown token '?' at position 5."),
                arrayOf(" 5 /variable+6&89", "Unknown token '&' at position 14.")
            )
        }
    }

    @Test
    fun `tokenize returns empty list when expression source is empty`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "")
        lexicalAnalyzer.tokenize().should.equal(emptyList<Token>())
    }

    @Test
    fun `tokenize returns a list with single digit integer token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "1")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.INTEGER, lexeme = "1")))
    }

    @Test
    fun `tokenize returns a list with multiple digit integer token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "1345")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.INTEGER, lexeme = "1345")))
    }

    @Test
    fun `tokenize returns a list with single char identifier token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "a")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = "a")))
    }

    @Test
    fun `tokenize returns a list with multiple char identifier token`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "variable")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = "variable")))
    }

    @ParameterizedTest
    @MethodSource("provideIdentifiersWithUnderscore")
    fun `tokenize returns a list with identifier token that contains underscore`(
        expressionSource: String,
        expectedLexeme: String
    ) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = expectedLexeme)))
    }

    @Test
    fun `tokenize returns a list with identifier token that contains uppercase letters`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "CONSTANT")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = "CONSTANT")))
    }

    @ParameterizedTest
    @MethodSource("provideSingleMathematicalOperatorExpressions")
    fun `tokenize returns a list with mathematical operator token`(expressionSource: String, expectedLexeme: String) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.MATH_OPERATOR, lexeme = expectedLexeme)))
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
}
