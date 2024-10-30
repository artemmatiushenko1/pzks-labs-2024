import net.oddpoet.expect.expect
import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
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

    @Test
    fun `parses simple 3-operand expression with additive and multiplicative operators`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2+3*1").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression(value = "2"),
                    operator = "+",
                    right = BinaryExpression(
                        left = NumberLiteralExpression(value = "3"),
                        operator = "*",
                        right = NumberLiteralExpression(value = "1")
                    )
                )
            )
        )
    }

    @Test
    fun `parses simple 4-operand expression with additive and multiplicative operators`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2*3-1/5").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = NumberLiteralExpression(value = "2"),
                        operator = "*",
                        right = NumberLiteralExpression(value = "3"),
                    ),
                    operator = "-",
                    right = BinaryExpression(
                        left = NumberLiteralExpression(value = "1"),
                        operator = "/",
                        right = NumberLiteralExpression(value = "5")
                    )
                )
            )
        )
    }

    @Test
    fun `parses simple 5-operand expression with additive and multiplicative operators`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "2*3-1/5.99+a").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = BinaryExpression(
                            left = NumberLiteralExpression(value = "2"),
                            operator = "*",
                            right = NumberLiteralExpression(value = "3"),
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = NumberLiteralExpression(value = "1"),
                            operator = "/",
                            right = NumberLiteralExpression(value = "5.99")
                        )
                    ),
                    operator = "+",
                    right = IdentifierExpression(value = "a")
                )
            )
        )
    }

    @Test
    fun `parses unary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "-1").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = UnaryExpression(
                    operator = "-",
                    argument = NumberLiteralExpression(value = "1")
                )
            )
        )
    }

    @Test
    fun `parses expression that starts with unary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "-2*3-1/5.99+a").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = BinaryExpression(
                            left = UnaryExpression(operator = "-", argument = NumberLiteralExpression("2")),
                            operator = "*",
                            right = NumberLiteralExpression(value = "3"),
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = NumberLiteralExpression(value = "1"),
                            operator = "/",
                            right = NumberLiteralExpression(value = "5.99")
                        )
                    ),
                    operator = "+",
                    right = IdentifierExpression(value = "a")
                )
            )
        )
    }

    @Test
    fun `parses paren expression with single numeric literal`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(6)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = ParenExpression(
                    expression = NumberLiteralExpression(value = "6")
                )
            )
        )
    }

    @Test
    fun `parses paren binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(6+4)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = ParenExpression(
                    expression = BinaryExpression(
                        left = NumberLiteralExpression(value = "6"),
                        operator = "+",
                        right = NumberLiteralExpression(value = "4"),
                    )
                )
            )
        )
    }

    @Test
    fun `parses paren binary expression multiplied by number literal`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(6+4)/a").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "6"),
                            operator = "+",
                            right = NumberLiteralExpression(value = "4"),
                        )
                    ),
                    operator = "/",
                    right = IdentifierExpression(value = "a")
                )
            )
        )
    }

    @Test
    fun `parses unary paren binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "-(6+a)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = UnaryExpression(
                    operator = "-",
                    argument = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "6"),
                            operator = "+",
                            right = IdentifierExpression(value = "a"),
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `parses unary expression as right part of multiplicative binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "a*-(6+a)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = IdentifierExpression(value = "a"),
                    operator = "*",
                    right = UnaryExpression(
                        operator = "-",
                        argument = ParenExpression(
                            expression = BinaryExpression(
                                left = NumberLiteralExpression(value = "6"),
                                operator = "+",
                                right = IdentifierExpression(value = "a"),
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `parses unary expression as right part of additive binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "a+-(6+a)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = IdentifierExpression(value = "a"),
                    operator = "+",
                    right = UnaryExpression(
                        operator = "-",
                        argument = ParenExpression(
                            expression = BinaryExpression(
                                left = NumberLiteralExpression(value = "6"),
                                operator = "+",
                                right = IdentifierExpression(value = "a"),
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `parses 2 additive paren expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "-(6+a)+(1.2*b)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = UnaryExpression(
                        operator = "-",
                        argument = ParenExpression(
                            expression = BinaryExpression(
                                left = NumberLiteralExpression(value = "6"),
                                operator = "+",
                                right = IdentifierExpression(value = "a"),
                            )
                        )
                    ),
                    operator = "+",
                    right = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "1.2"),
                            operator = "*",
                            right = IdentifierExpression(value = "b")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `throws when there's a duplicate additive operator`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "a++(6+b)").tokenize()
        expect { Parser(tokens = tokens.toMutableList()).parse() }.throws(Exception::class) { it.message.should.equal("Unexpected token!") }
    }

    @Test
    fun `parses multiplicative binary expression with paren operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(2+1)*(3.3-b)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "2"),
                            operator = "+",
                            right = NumberLiteralExpression(value = "1")
                        )
                    ),
                    operator = "*",
                    right = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "3.3"),
                            operator = "-",
                            right = IdentifierExpression(value = "b")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `parses nested paren expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "((2+v))").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = ParenExpression(
                    expression = ParenExpression(
                        expression = BinaryExpression(
                            left = NumberLiteralExpression(value = "2"),
                            operator = "+",
                            right = IdentifierExpression(value = "v")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `test parses complex nested paren expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(((-(2+v)))*3)").tokenize()
        val ast = Parser(tokens = tokens.toMutableList()).parse()

        ast.should.equal(
            ExpressionStatement(
                expression = ParenExpression(
                    expression = BinaryExpression(
                        left = ParenExpression(
                            expression = ParenExpression(
                                expression = UnaryExpression(
                                    operator = "-",
                                    argument = ParenExpression(
                                        expression = BinaryExpression(
                                            left = NumberLiteralExpression(
                                                "2"
                                            ),
                                            operator = "+",
                                            right = IdentifierExpression(value = "v")
                                        )
                                    )
                                )
                            )
                        ),
                        operator = "*",
                        right = NumberLiteralExpression(value = "3")
                    )
                )
            )
        )
    }
}
