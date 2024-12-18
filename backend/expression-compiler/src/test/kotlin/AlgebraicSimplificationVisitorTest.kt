import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.generateAst
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.*
import org.example.visitors.AlgebraicSimplificationVisitor
import org.example.visitors.ConstantFoldingVisitor
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class AlgebraicSimplificationVisitorTest {
    @Test
    fun `simplifies expressions with multiplication by 0 with 0 on the right`() {
        val ast = generateAst("(2+n)*0+1")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(NumberLiteralExpression("1"))
    }

    @Test
    fun `simplifies expressions with multiplication by 0 with 0 on the left`() {
        val ast = generateAst("0*(4+n)+8")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(NumberLiteralExpression("8"))
    }

    @Test
    fun `simplifies nested multiplication by 0`() {
        val ast = generateAst("2*(3*0)*4")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(NumberLiteralExpression("0"))
    }

    @Test
    fun `simplifies multiplication by 0 within addition`() {
        val ast = generateAst("n+(3*0)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            IdentifierExpression("n")
        )
    }

    @Test
    fun `simplifies multiplication by 0 with zero in the middle`() {
        val ast = generateAst("(2+3)*0*(n+1)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            NumberLiteralExpression("0")
        )
    }

    @Test
    fun `simplifies multiplication by 0 in nested paren expression`() {
        val ast = generateAst("(0*(n+5))*(m+3)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(NumberLiteralExpression("0"))
    }

    @Test
    fun `simplifies multiplication by 0 in expression with multiple expression multiplied by 0`() {
        val ast = generateAst("(a*0)+(b*0)+c")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(IdentifierExpression("c"))
    }

    @Test
    fun `simplifies simple zero divided by expression`() {
        val ast = generateAst("0/(a*0)+c")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(IdentifierExpression("c"))
    }

    @Test
    fun `simplifies complex zero divided by expression`() {
        val ast = generateAst("0/(a*9-34/(-2))+(c+0/a-(0/9))-(0/-(a+c))")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(ParenExpression(IdentifierExpression("c")))
    }

    @Test
    fun `simplifies expression with division with 1`() {
        val ast = generateAst("(2+3)/1-1/4/1+(-9/1)/1-((a+b)/1)") // (2+3)-1/4+(-9)-((a+b))
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            BinaryExpression(
                left = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("2"),
                                operator = "+",
                                right = NumberLiteralExpression("3")
                            )
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = NumberLiteralExpression("1"),
                            operator = "/",
                            right = NumberLiteralExpression("4")
                        )
                    ),
                    operator = "+",
                    right = ParenExpression(
                        BinaryExpression(
                            left = NumberLiteralExpression("0"),
                            operator = "-",
                            right = NumberLiteralExpression("9")
                        )
                    )
                ),
                operator = "-",
                right = ParenExpression(
                    ParenExpression(
                        BinaryExpression(
                            left = IdentifierExpression("a"),
                            operator = "+",
                            right = IdentifierExpression("b"),
                        )
                    )
                ),
            )
        )
    }

    @Test
    fun `simplifies multiplication by 1`() {
        val ast = generateAst("(2+3)*1-1*4/a+(-9*1)*1-((a+b)*1)") // (2+3)-4/a+(-9)-((a+b))
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            BinaryExpression(
                left = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("2"),
                                operator = "+",
                                right = NumberLiteralExpression("3")
                            )
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = NumberLiteralExpression("4"),
                            operator = "/",
                            right = IdentifierExpression("a")
                        )
                    ),
                    operator = "+",
                    right = ParenExpression(
                        BinaryExpression(
                            left = NumberLiteralExpression("0"),
                            operator = "-",
                            right = NumberLiteralExpression("9")
                        )
                    )
                ),
                operator = "-",
                right = ParenExpression(
                    ParenExpression(
                        BinaryExpression(
                            left = IdentifierExpression("a"),
                            operator = "+",
                            right = IdentifierExpression("b"),
                        )
                    )
                ),
            )
        )
    }

    @Test
    fun `simplifies additive expressions with 0 as operand`() {
        val ast = generateAst("(2+3)+0-(0+4)/a+(-9-0)+0-(0-(a+b)+0)") // (2+3)-(4)/a+(-9)-(0-(a+b))
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        assertEquals(
            BinaryExpression(
                left = BinaryExpression(
                    left = BinaryExpression(
                        left = ParenExpression(
                            BinaryExpression(
                                left = NumberLiteralExpression("2"),
                                operator = "+",
                                right = NumberLiteralExpression("3")
                            )
                        ),
                        operator = "-",
                        right = BinaryExpression(
                            left = ParenExpression(NumberLiteralExpression("4")),
                            operator = "/",
                            right = IdentifierExpression("a")
                        )
                    ),
                    operator = "+",
                    right = ParenExpression(
                        BinaryExpression(
                            left = NumberLiteralExpression("0"),
                            operator = "-",
                            right = NumberLiteralExpression("9")
                        )
                    )
                ),
                operator = "-",
                right = ParenExpression(
                    BinaryExpression(
                        left = NumberLiteralExpression("0"),
                        operator = "-",
                        right = ParenExpression(
                            BinaryExpression(
                                left = IdentifierExpression("a"),
                                operator = "+",
                                right = IdentifierExpression("b"),
                            )
                        )
                    )
                ),
            ),
            simplifiedAst
        )
    }

    @Test
    fun `returns 1 for division of two equal expressions`() {
        val ast = generateAst("(2/b)/(2/b)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())
        simplifiedAst.should.equal(NumberLiteralExpression("1"))
    }

    @Test
    fun `replaces division with multiplication in expression with sequential division`() {
        val ast = generateAst("a/b/c") // a/(b*c)
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            BinaryExpression(
                left = IdentifierExpression("a"),
                operator = "/",
                right = BinaryExpression(
                    left = IdentifierExpression("b"),
                    operator = "*",
                    right = IdentifierExpression("c")
                )
            )
        )
    }

    @Test
    fun `eliminates nested unary expression if they have the same operator`() {
        val ast = generateAst("-(-5)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(ParenExpression(NumberLiteralExpression("5")))
    }

    @Test
    fun `converts unary to binary expression by backfilling zero`() {
        val ast = generateAst("-(b+c)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            BinaryExpression(
                left = NumberLiteralExpression("0"),
                operator = "-",
                right = ParenExpression(
                    BinaryExpression(
                        left = IdentifierExpression("b"),
                        operator = "+",
                        right = IdentifierExpression("c")
                    )
                )
            )
        )
    }

    @Test
    @Ignore
    fun `eliminates unary expression if it has + operator`() {
        val ast = generateAst("+(3-5)")
        val simplifiedAst = ast?.accept(AlgebraicSimplificationVisitor())

        simplifiedAst.should.equal(
            ParenExpression(
                BinaryExpression(
                    left = NumberLiteralExpression("3"),
                    operator = "-",
                    right = NumberLiteralExpression("5")
                )
            )
        )
    }
}
