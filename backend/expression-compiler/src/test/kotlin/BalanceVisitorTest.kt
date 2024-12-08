import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.generateAst
import org.example.parser.*
import org.example.visitors.BalanceVisitor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BalanceVisitorTest {
    @Test
    fun `balances ast with sequential addition`() {
        // a+b+c+d
        val ast = BinaryExpression(
            left = BinaryExpression(
                left = BinaryExpression(
                    left = IdentifierExpression("a"),
                    operator = "+",
                    right = IdentifierExpression("b")
                ),
                operator = "+",
                right = IdentifierExpression("c")
            ),
            operator = "+",
            right = IdentifierExpression("d"),
        )

        val balancedAst = ast.accept(BalanceVisitor())

        balancedAst.should.equal(
            BinaryExpression(
                left = BinaryExpression(
                    left = IdentifierExpression("a"),
                    operator = "+",
                    right = IdentifierExpression("b")
                ),
                operator = "+",
                right = BinaryExpression(
                    left = IdentifierExpression("c"),
                    operator = "+",
                    right = IdentifierExpression("d")
                ),
            )
        )
    }

    @Test
    fun `balances ast with sequential addition on right`() {
        // d+(a+b+c+e)
        val ast = BinaryExpression(
            left = IdentifierExpression("d"),
            operator = "+",
            right = ParenExpression(
                BinaryExpression(
                    left = BinaryExpression(
                        left = BinaryExpression(
                            left = IdentifierExpression("a"),
                            operator = "+",
                            right = IdentifierExpression("b")
                        ),
                        operator = "+",
                        right = IdentifierExpression("c")
                    ),
                    operator = "+",
                    right = IdentifierExpression("e")
                )
            ),
        )

        val balancedAst = ast.accept(BalanceVisitor())

        assertEquals(
            BinaryExpression(
                left = IdentifierExpression("d"),
                operator = "+",
                right = ParenExpression(
                    BinaryExpression(
                        left = BinaryExpression(
                            left = IdentifierExpression("a"),
                            operator = "+",
                            right = IdentifierExpression("b")
                        ),
                        operator = "+",
                        right = BinaryExpression(
                            left = IdentifierExpression("c"),
                            operator = "+",
                            right = IdentifierExpression("e")
                        )
                    )
                ),
            ),
            balancedAst
        )
    }

    @Test
    fun `balances ast with sequential addition with more operands `() {
        val ast = generateAst("a+b+c+d+e+f")
        val balancedAst = ast?.accept(BalanceVisitor())

        assertEquals(
            BinaryExpression(
                left = BinaryExpression(
                    left = BinaryExpression(
                        left = IdentifierExpression("a"),
                        operator = "+",
                        right = IdentifierExpression("b"),
                    ),
                    operator = "+",
                    right = IdentifierExpression("c")
                ),
                operator = "+",
                right = BinaryExpression(
                    left = IdentifierExpression("d"),
                    operator = "+",
                    right = BinaryExpression(
                        left = IdentifierExpression("e"),
                        operator = "+",
                        right = IdentifierExpression("f")
                    )
                )
            ),
            balancedAst
        )
    }
}
