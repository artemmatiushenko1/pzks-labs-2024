import net.oddpoet.expect.expect
import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.*
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.ConstantFoldingVisitor
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Ignore

class ConstantFoldingVisitorTest {
    @Test
    fun `folds constants on sum expression with 2 operands`() {
        val ast = generateAst("2+2")

        val expectedAst = NumberLiteralExpression("4")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on subtraction expression with 2 operands`() {
        val ast = generateAst("4-2")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on multiplication expression with 2 operands`() {
        val ast = generateAst("4*2")

        val expectedAst = NumberLiteralExpression("8")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on division expression with 2 operands`() {
        val ast = generateAst("10/5")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on sum expression with 3 operands`() {
        val ast = generateAst("2+2+10")

        val expectedAst = NumberLiteralExpression("14")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on subtraction expression with 3 operands`() {
        val ast = generateAst("15-2-10")

        val expectedAst = NumberLiteralExpression("3")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on multiplication expression with 3 operands`() {
        val ast = generateAst("2*2*2")

        val expectedAst = NumberLiteralExpression("8")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on division expression with 3 operands`() {
        val ast = generateAst("8/2/2")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on expression with unary number literal expression`() {
        val ast = generateAst("-1+2*4")

        val expectedAst = NumberLiteralExpression("7")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `folds constants on expression with paren exp on left`() {
        val ast = generateAst("(1+2)*4")
        val foldedAst = ExpressionStatement(expression = ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("12")
            )
        )
    }

    @Test
    fun `folds constants on expression with paren exp on right`() {
        val ast = generateAst("12/(1+2)")
        val foldedAst = ExpressionStatement(expression = ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("4")
            )
        )
    }

    @Test
    fun `does not change ast when there are no constants to fold`() {
        val ast = generateAst("((1+n)+b+7)")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))
        foldedAst.should.equal(ast)
    }

    @Test
    fun `folds constants on expression with sequential addition`() {
        val ast = generateAst("1+7+2+1+88")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression(value = "99")
            )
        )
    }

    @Test
    fun `folds constants on sequential addition with identifier expression at start`() {
        val ast = generateAst("a+2+1+5+6")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = IdentifierExpression("a"),
                    operator = "+",
                    right = NumberLiteralExpression("14")
                )
            )
        )
    }

    @Test
    fun `folds constants on sequential addition with identifier expression in the end`() {
        val ast = generateAst("2+1+7+a")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression("10"),
                    operator = "+",
                    right = IdentifierExpression("a")
                ),
            )
        )
    }

    @Test
    fun `folds constants on sequential addition with identifier expression in the middle`() {
        val ast = generateAst("2+1+a+7+3")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = NumberLiteralExpression("3"),
                        operator = "+",
                        right = IdentifierExpression("a")
                    ),
                    operator = "+",
                    right = NumberLiteralExpression("10")
                )
            )
        )
    }

    @Test
    fun `folds constants on sequential addition with unary number expression on the start`() {
        val ast = generateAst("-1+7+2")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(ExpressionStatement(expression = NumberLiteralExpression("8")))
    }

    @Test
    fun `folds constants on sequential addition with paren expression`() {
        val ast = generateAst("(-1+7+1)+2+4")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("13")
            )
        )
    }

    @Test
    fun `folds constants in unary paren expression`() {
        val ast = generateAst("-(2+2-3*3)")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = UnaryExpression(
                    "-",
                    argument = ParenExpression(
                        UnaryExpression(
                            operator = "-",
                            argument = NumberLiteralExpression("5")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `folds constants in paren unary paren expression`() {
        val ast = generateAst("(-(2+2-3*3))")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = ParenExpression(
                    UnaryExpression(
                        operator = "-",
                        argument = ParenExpression(
                            UnaryExpression(
                                operator = "-",
                                argument = NumberLiteralExpression("5")
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `folds constants into unary expression`() {
        val ast = generateAst("2+2+10-20")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = UnaryExpression(operator = "-", argument = NumberLiteralExpression("6"))
            )
        )
    }

    @Test
    fun `folds constants into correct expression with subtraction on the right`() {
        val ast = generateAst("(2+2)-10-20")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = UnaryExpression(operator = "-", argument = NumberLiteralExpression("26"))
            )
        )
    }

    @Test
    fun `folds constants in complex expression`() {
        val ast = generateAst("((1+3)/a)-b*(-1+7+1-1)-2*4")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("4"),
                                operator = "/",
                                right = IdentifierExpression("a")
                            )
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = IdentifierExpression("b"),
                            operator = "*",
                            right = NumberLiteralExpression("6")
                        )
                    ),
                    operator = "-",
                    right = NumberLiteralExpression("8")
                )
            )
        )
    }

    @Test
    fun `does not fold constants that are part of multiplicative expression`() {
        val ast = generateAst("(1+n)*0+1")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("1"),
                                operator = "+",
                                right = IdentifierExpression("n")
                            )
                        ),
                        operator = "*",
                        right = NumberLiteralExpression("0")
                    ),
                    operator = "+",
                    right = NumberLiteralExpression("1")
                )
            )
        )
    }

    @Test
    fun `does not fold constants that are part of multiplicative expression with division`() {
        val ast = generateAst("(1+n)/7+1")
        val foldedAst = ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor()))

        foldedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("1"),
                                operator = "+",
                                right = IdentifierExpression("n")
                            )
                        ),
                        operator = "/",
                        right = NumberLiteralExpression("7")
                    ),
                    operator = "+",
                    right = NumberLiteralExpression("1")
                )
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["(a*0)+(b*0)/0", "2/0", "((a*0)+(b*0))/0", "(a*0)/0+(b*0)", "-(3)/0", "-3/0"])
    fun `forbids division by zero`(expressionSource: String) {
        val ast = generateAst(expressionSource)
        expect { ExpressionStatement(ast.expression?.accept(ConstantFoldingVisitor())) }.throws(
            IllegalArgumentException::class
        )
    }
}
