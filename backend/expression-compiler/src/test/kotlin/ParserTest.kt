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
    fun `parses simple binary expression with addition`() {
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

    @Test
    fun `parses simple binary expression with subtraction`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2-3").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression(value = "2"),
                    operator = "-",
                    right = NumberLiteralExpression(value = "3"),
                )
            )
        )
    }

    @Test
    fun `parses additive expression with 3 operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2+a+4").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = NumberLiteralExpression(value = "2"),
                        operator = "+",
                        right = IdentifierExpression(value = "a")
                    ),
                    operator = "+",
                    right = NumberLiteralExpression(value = "4"),
                )
            )
        )
    }

    @Test
    fun `parses additive expression with 4 operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2+a+4-7.9").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = BinaryExpression(
                            left = NumberLiteralExpression(value = "2"),
                            operator = "+",
                            right = IdentifierExpression(value = "a")
                        ),
                        operator = "+",
                        right = NumberLiteralExpression(value = "4")
                    ),
                    operator = "-",
                    right = NumberLiteralExpression(value = "7.9"),
                )
            )
        )
    }

    @Test
    fun `parses simple binary expression with multiplication`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2*4").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression(value = "2"),
                    operator = "*",
                    right = NumberLiteralExpression(value = "4")
                )
            )
        )
    }

    @Test
    fun `parses simple binary expression with division`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2/4").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression(value = "2"),
                    operator = "/",
                    right = NumberLiteralExpression(value = "4")
                )
            )
        )
    }

    @Test
    fun `parses multiplicative expression with 4 operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2*4/6*9").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = BinaryExpression(
                            left = NumberLiteralExpression(value = "2"),
                            operator = "*",
                            right = NumberLiteralExpression(value = "4")
                        ),
                        operator = "/",
                        right = NumberLiteralExpression(value = "6")
                    ),
                    operator = "*",
                    right = NumberLiteralExpression(value = "9")
                )
            )
        )
    }
}
