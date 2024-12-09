import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.generateAst
import org.example.parser.*
import org.example.visitors.RedundantParensRemovalVisitor
import org.junit.jupiter.api.Test

class RedundantParensRemovalVisitorTest {
    @Test
    fun `removes redundant parens`() {
        val ast = generateAst("(2+2)+(((a-3)))")
        val simplifiedExpr = ast?.accept(RedundantParensRemovalVisitor())

        simplifiedExpr.should.equal(
            BinaryExpression(
                left = ParenExpression(
                    BinaryExpression(
                        left = NumberLiteralExpression("2"),
                        operator = "+",
                        right = NumberLiteralExpression("2")
                    )
                ),
                operator = "+",
                right = ParenExpression(
                    BinaryExpression(
                        left = IdentifierExpression("a"),
                        operator = "-",
                        right = NumberLiteralExpression("3")
                    )
                )
            )
        )
    }

    @Test
    fun `removes redundant parens from unary expression`() {
        val ast = generateAst("-((a+b))")
        val simplifiedExpr = ast?.accept(RedundantParensRemovalVisitor())

        simplifiedExpr.should.equal(
            UnaryExpression(
                operator = "-",
                ParenExpression(
                    BinaryExpression(
                        left = IdentifierExpression("a"),
                        operator = "+",
                        right = IdentifierExpression("b")
                    )
                )
            )
        )
    }

    @Test
    fun `removes redundant parens from number literal expression`() {
        val ast = generateAst("(2)+(4)")
        val simplifiedExpr = ast?.accept(RedundantParensRemovalVisitor())

        simplifiedExpr.should.equal(
            BinaryExpression(
                left = NumberLiteralExpression("2"),
                operator = "+",
                right = NumberLiteralExpression("4")
            )
        )
    }

    @Test
    fun `removes redundant parens from identifier expression`() {
        val ast = generateAst("((a))+(((b)))")
        val simplifiedExpr = ast?.accept(RedundantParensRemovalVisitor())

        simplifiedExpr.should.equal(
            BinaryExpression(
                left = IdentifierExpression("a"),
                operator = "+",
                right = IdentifierExpression("b")
            )
        )
    }
}
