import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.SyntaxAnalyzerImpl
import org.example.SyntaxError
import org.example.Token
import org.example.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SyntaxAnalyzerImplTest {
    companion object {
        @JvmStatic
        fun provideTokensWithBadTokensPrecedingOrFollowingMathOperator(): List<List<Token>> {
            return listOf(
                listOf(
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "/", position = 0),
                    Token(type = TokenType.NUMBER, lexeme = "2", position = 1),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "+", position = 2),
                    Token(type = TokenType.NUMBER, lexeme = "2", position = 3)
                ),
                listOf(
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "*", position = 0),
                    Token(type = TokenType.NUMBER, lexeme = "2", position = 1),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "-", position = 1),
                    Token(type = TokenType.IDENTIFIER, lexeme = "a", position = 1),
                ),
                listOf(
                    Token(type = TokenType.IDENTIFIER, lexeme = "variable", position = 0),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "*", position = 1),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "*", position = 1),
                    Token(type = TokenType.NUMBER, lexeme = "2", position = 1),
                ),
                listOf(
                    Token(type = TokenType.IDENTIFIER, lexeme = "(", position = 0),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "*", position = 1),
                    Token(type = TokenType.MATH_OPERATOR, lexeme = "*", position = 1),
                    Token(type = TokenType.NUMBER, lexeme = "2", position = 1),
                ),
            )
        }
    }

    @Test
    fun `returns a list with error when math operator is not followed by a correct operand`() {
        val tokens = listOf(
            Token(type = TokenType.NUMBER, lexeme = "2", position = 0),
            Token(type = TokenType.MATH_OPERATOR, lexeme = "+", position = 1)
        )

        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [number, identifier, open_paren] after '+' at position 2.",
                    position = 2
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithBadTokensPrecedingOrFollowingMathOperator")
    fun `returns a list with error when math operator is not preceded by a correct operand`(tokens: List<Token>) {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting '*' to be preceded by one of the following [number, identifier, close_paren] at position -1.",
                    position = -1
                )
            )
        )
    }
}
