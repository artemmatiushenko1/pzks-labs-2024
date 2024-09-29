import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.SyntaxAnalyzerImpl
import org.example.SyntaxError
import org.example.Token
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

private fun String.toTokens(): List<Token> {
    return LexicalAnalyzerImpl(expressionSource = this).tokenize()
}

class SyntaxAnalyzerImplTest {
    companion object {
        @JvmStatic
        fun provideTokensWithBadTokensPrecedingOrFollowingMathOperator(): List<Pair<List<Token>, SyntaxError>> {
            return listOf(
                "variable**2".toTokens() to SyntaxError(
                    "Expecting one of the following [number, identifier, open_paren] after '*' at position 9.",
                    position = 9
                ),
                "a*/2".toTokens() to SyntaxError(
                    "Expecting one of the following [number, identifier, open_paren] after '*' at position 2.",
                    position = 2
                ),
                "a/*".toTokens() to SyntaxError(
                    "Expecting one of the following [number, identifier, open_paren] after '/' at position 2.",
                    position = 2
                ),
                "2+4+-3+a".toTokens() to SyntaxError(
                    "Expecting one of the following [number, identifier, open_paren] after '+' at position 4.",
                    position = 4
                ),
//                "2+4+--+3+a".toTokens() to SyntaxError(
//                    "Expecting one of the following [number, identifier, open_paren] after '+' at position 4.",
//                    position = 4
//                )
            )
        }

        @JvmStatic
        fun provideTokensWithWrongStartingToken(): List<List<Token>> {
            return listOf("/1+34+a".toTokens(), "*1+34+a".toTokens(), ")1+34+a".toTokens())
        }
    }

    @Test
    fun `returns a list with error when + is not followed by a correct operand`() {
        val tokens = "2+".toTokens()

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

    @Test
    fun `doesn't consider - operator before number as error`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "-2+2".toTokens())
        syntaxAnalyzer.analyze().should.equal(emptyList<String>())
    }

    @Test
    fun `doesn't consider + operator before number as error`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "+2+2".toTokens())
        syntaxAnalyzer.analyze().should.equal(emptyList<String>())
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithBadTokensPrecedingOrFollowingMathOperator")
    fun `returns a list with error when math operator is not preceded by a correct operand`(tokensToError: Pair<List<Token>, SyntaxError>) {
        val (tokens, error) = tokensToError
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.equal(
            listOf(error)
        )
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithWrongStartingToken")
    fun `returns a list with error for tokens list with wrong starting token`(tokens: List<Token>) {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expression should start with one of the following [number, identifier, open_paren].",
                    position = 0
                )
            )
        )
    }
}
