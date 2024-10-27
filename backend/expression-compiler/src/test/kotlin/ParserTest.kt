import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.parser.*
import kotlin.test.Test


class ParserTest {
    @Test
    fun `returns empty expression statement when there are no tokens provided`() {
        val ast = Parser(tokens = mutableListOf()).parse()
        ast.should.equal(ExpressionStatement(expression = null))
    }

    @Test
    fun `parses number literal expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "345").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()
        ast.should.equal(ExpressionStatement(expression = NumberLiteralExpression(value = "345")))
    }

    @Test
    fun `parses identifier expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "a").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()
        ast.should.equal(ExpressionStatement(expression = IdentifierExpression(value = "a")))
    }

    @Test
    fun `parser simple binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2+3").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression(value = "2"),
                    operator = "+",
                    right = NumberLiteralExpression(value = "3"),
                )
            )
        )
    }
}
