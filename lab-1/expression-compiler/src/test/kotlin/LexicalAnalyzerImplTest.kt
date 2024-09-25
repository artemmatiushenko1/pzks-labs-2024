import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.Token
import org.example.TokenType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSources
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

    @Test
    fun `tokenize returns a list with identifier token that starts with underscore`() {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = "_variable")
        lexicalAnalyzer.tokenize().should.equal(listOf(Token(type = TokenType.IDENTIFIER, lexeme = "_variable")))
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
    fun `tokenize returns correct tokens for a 2+2 expression`(expressionSource: String, expectedTokens: List<Token>) {
        val lexicalAnalyzer = LexicalAnalyzerImpl(expressionSource = expressionSource)
        lexicalAnalyzer.tokenize().should.equal(expectedTokens)
    }
}
