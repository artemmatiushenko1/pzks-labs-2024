import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.SyntaxAnalyzerImpl
import org.example.SyntaxError
import org.example.Token
import org.example.TokenType
import org.junit.jupiter.api.Test

class SyntaxAnalyzerImplTest {
    @Test
    fun `throws an error when math operator is not followed by a correct operand`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl()
        val tokens = listOf(
            Token(type = TokenType.NUMBER, lexeme = "2", position = 0),
            Token(type = TokenType.MATH_OPERATOR, lexeme = "+", position = 1)
        )

        syntaxAnalyzer.analyze(tokens).should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [number, identifier, close_paren] after '+' at position 2.",
                    position = 2
                )
            )
        )
    }
}
