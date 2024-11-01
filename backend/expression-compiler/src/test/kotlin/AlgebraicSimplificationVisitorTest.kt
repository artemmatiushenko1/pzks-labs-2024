import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.*
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.ConstantFoldingVisitor
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class AlgebraicSimplificationVisitorTest {
    private fun generateAst(expressionSource: String): ExpressionStatement {
        val tokens = LexicalAnalyzerImpl(expressionSource = expressionSource).tokenize()
        val ast = Parser(tokens = tokens).parse()
        return ast
    }

    @Test
    fun `eliminates expressions with multiplication by 0 with 0 on the right`() {
        val ast = generateAst("(2+n)*0+1")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression("0"),
                    operator = "+",
                    right = NumberLiteralExpression("1")
                )
            )
        )
    }

    @Test
    fun `eliminates expressions with multiplication by 0 with 0 on the left`() {
        val ast = generateAst("0*(4+n)+8")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = NumberLiteralExpression("0"),
                    operator = "+",
                    right = NumberLiteralExpression("8")
                )
            )
        )
    }

    @Test
    fun `eliminates nested multiplication by 0`() {
        val ast = generateAst("2*(3*0)*4")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("0")
            )
        )
    }

    @Test
    fun `eliminates multiplication by 0 within addition`() {
        val ast = generateAst("n+(3*0)")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = IdentifierExpression("n"),
                    operator = "+",
                    right = NumberLiteralExpression("0")
                )
            )
        )
    }

    @Test
    fun `eliminates multiplication by 0 with zero in the middle`() {
        val ast = generateAst("(2+3)*0*(n+1)")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("0")
            )
        )
    }

    @Test
    fun `eliminates multiplication by 0 in nested paren expression`() {
        val ast = generateAst("(0*(n+5))*(m+3)")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = NumberLiteralExpression("0")
            )
        )
    }

    @Test
    fun `eliminates multiplication by 0 in expression with multiple expression multiplied by 0`() {
        val ast = generateAst("(a*0)+(b*0)+c")
        val simplifiedAst = ExpressionStatement(ast.expression?.accept(AlgebraicSimplificationVisitor()))

        simplifiedAst.should.equal(
            ExpressionStatement(
                expression = BinaryExpression(
                    left = BinaryExpression(
                        left = NumberLiteralExpression("0"),
                        operator = "+",
                        right = NumberLiteralExpression("0")
                    ),
                    operator = "+",
                    right = IdentifierExpression("c")
                )
            )
        )
    }
}
