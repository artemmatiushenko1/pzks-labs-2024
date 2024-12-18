import net.oddpoet.expect.extension.beEmpty
import net.oddpoet.expect.extension.contain
import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.syntaxAnalyzer.SyntaxAnalyzerImpl
import org.example.syntaxAnalyzer.SyntaxError
import org.example.lexicalAnalyzer.Token
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
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '*'.",
                    position = 9
                ),
                "a*/2".toTokens() to SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '*'.",
                    position = 2
                ),
                "a/*7".toTokens() to SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '/'.",
                    position = 2
                ),
                "2+4+-3+a".toTokens() to SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '+'.",
                    position = 4
                ),
            )
        }

        @JvmStatic
        fun provideTokensWithWrongStartingToken(): List<List<Token>> {
            return listOf("/1+34+a".toTokens(), "*1+34+a".toTokens(), ")1+34+a".toTokens())
        }

        @JvmStatic
        fun provideTokensWithUnbalancedParenthesis(): List<List<Token>> {
            return listOf(
                "((a+(b)".toTokens(),
                "((b)+(2*3-5/7)))".toTokens(),
                ")(())()".toTokens(),
                ")(".toTokens(),
                "()))".toTokens()
            )
        }

        @JvmStatic
        fun provideTokensWithEmptyParenthesisPair(): List<Pair<List<Token>, Int>> {
            return listOf(
                "a+()".toTokens() to 3,
                "()+3.45/d".toTokens() to 1,
                "var+45*()-3".toTokens() to 8,
                "a+45*(()".toTokens() to 7,
            )
        }

        @JvmStatic
        fun provideTokensWithWrongTokenAfterNumberToken(): List<Pair<List<Token>, SyntaxError>> {
            return listOf(
                "1+3var+2".toTokens() to SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after '3'.",
                    position = 3
                ),
                "45.6(*1".toTokens() to SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after '45.6'.",
                    position = 4
                ),
                "1(2+2)".toTokens() to SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after '1'.",
                    position = 1
                ),
            )
        }

        @JvmStatic
        fun provideTokensWithWrongTokenAfterIdentifier(): List<Pair<List<Token>, SyntaxError>> {
            return listOf(
                "1+a 2".toTokens() to SyntaxError(
                    "Expecting one of the following [math_operator, close_paren] after 'a'.",
                    position = 4
                ),
                "1+a_(2+2)".toTokens() to SyntaxError(
                    "Expecting one of the following [math_operator, close_paren] after 'a_'.",
                    position = 4
                )
            )
        }
    }

    @Test
    fun `returns a list with error when + is not followed by a correct operand`() {
        val tokens = "2+-a".toTokens()

        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '+'.",
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

        syntaxAnalyzer.analyze().should.contain(
            SyntaxError(
                "Expression should start with one of the following [NUMBER, IDENTIFIER, OPEN_PAREN, ADDITIVE_OPERATOR].",
                position = 0
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithUnbalancedParenthesis")
    fun `returns a list with error for tokens list with unbalanced parenthesis`(tokens: List<Token>) {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.contain(
            SyntaxError(
                "Parentheses mismatch.",
                position = null
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithEmptyParenthesisPair")
    fun `returns a list with error for tokens list with empty parenthesis pair`(tokensToErrorPosition: Pair<List<Token>, Int>) {
        val (tokens, errorPosition) = tokensToErrorPosition
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.contain(
            SyntaxError(
                "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN, ADDITIVE_OPERATOR] after '('.",
                position = errorPosition
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithWrongTokenAfterNumberToken")
    fun `returns a list with error for tokens list when number token is followed by wrong token`(tokensToError: Pair<List<Token>, SyntaxError>) {
        val (tokens, error) = tokensToError
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = tokens)

        syntaxAnalyzer.analyze().should.contain(error)
    }

    @Test
    fun `allows number token to be followed by math operation`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "1+a+(2+2)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `allows number token to be followed by close parenthesis`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "2+2)".toTokens())
        syntaxAnalyzer.analyze().should.equal(listOf(SyntaxError(message = "Parentheses mismatch.", position = null)))
    }

    @ParameterizedTest
    @MethodSource("provideTokensWithWrongTokenAfterIdentifier")
    fun `returns a list with error for when identifier is followed by wrong token`(tokensToError: Pair<List<Token>, SyntaxError>) {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "1+a 2".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after 'a'.",
                    position = 4
                )
            )
        )
    }

    @Test
    fun `allows identifier token to be followed by math operation`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "1+identifier+(2+2)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `allows identifier token to be followed by close parenthesis`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "1+(2+identifier)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `returns a list with error for when close parenthesis is followed by wrong token`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "(2.34+3)(3*2)".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after ')'.",
                    position = 8
                )
            )
        )
    }

    @Test
    fun `allows parenthesis to be followed by another parenthesis`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "((2.34+3))+(3*2)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `returns a list with error for when open parenthesis is followed by a wrong token`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "((*2.34+3))+(3*2)".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN, ADDITIVE_OPERATOR] after '('.",
                    position = 2
                )
            )
        )
    }

    @Test
    fun `allows open parenthesis to be followed by + operator`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "((+2.34+3))+(3*2)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `allows open parenthesis to be followed by - operator`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "((-2.34+3))+(3*2)".toTokens())
        syntaxAnalyzer.analyze().should.beEmpty()
    }

    @Test
    fun `skips validation of wrong token after open parenthesis`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "2+(**g)-".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN, ADDITIVE_OPERATOR] after '('.",
                    position = 3
                ),
                SyntaxError(
                    "Expression should end with one of the following [CLOSE_PAREN, IDENTIFIER, NUMBER].",
                    position = 7
                )
            )
        )
    }

    @Test
    fun `skips validation of wrong token after number`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "2(*(*g))-".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError(
                    "Expecting one of the following [CLOSE_PAREN, ADDITIVE_OPERATOR, MULTIPLICATIVE_OPERATOR] after '2'.",
                    position = 1
                ),
                SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN, ADDITIVE_OPERATOR] after '('.",
                    position = 4
                ),
                SyntaxError(
                    "Expression should end with one of the following [CLOSE_PAREN, IDENTIFIER, NUMBER].",
                    position = 8
                )
            )
        )
    }

    @Test // TODO: add more test cases like this
    fun `produces correct errors`() {
        val syntaxAnalyzer = SyntaxAnalyzerImpl(tokens = "(-2+2)**9+(a-6/variable)-+".toTokens())
        syntaxAnalyzer.analyze().should.equal(
            listOf(
                SyntaxError("Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '*'.", position = 7),
                SyntaxError(
                    "Expecting one of the following [IDENTIFIER, NUMBER, OPEN_PAREN] after '-'.",
                    position = 25
                ),
                SyntaxError(
                    "Expression should end with one of the following [CLOSE_PAREN, IDENTIFIER, NUMBER].",
                    position = 25
                ),
            )
        )
    }
}
