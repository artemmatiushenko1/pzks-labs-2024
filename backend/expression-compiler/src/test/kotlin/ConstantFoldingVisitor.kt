import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.ExpressionStatement
import org.example.parser.NumberLiteralExpression
import org.example.parser.Parser
import org.example.parser.visitors.ConstantFoldingVisitor
import org.junit.jupiter.api.Test

class ConstantFoldingVisitor {
    private fun generateAst(expressionSource: String): ExpressionStatement {
        val tokens = LexicalAnalyzerImpl(expressionSource = expressionSource).tokenize()
        val ast = Parser(tokens = tokens).parse()
        return ast
    }

    @Test
    fun `performs constants folding on sum expression with 2 operands`() {
        val ast = generateAst("2+2")

        val expectedAst = NumberLiteralExpression("4")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on subtraction expression with 2 operands`() {
        val ast = generateAst("4-2")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on multiplication expression with 2 operands`() {
        val ast = generateAst("4*2")

        val expectedAst = NumberLiteralExpression("8")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on division expression with 2 operands`() {
        val ast = generateAst("10/5")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on sum expression with 3 operands`() {
        val ast = generateAst("2+2+10")

        val expectedAst = NumberLiteralExpression("14")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on subtraction expression with 3 operands`() {
        val ast = generateAst("15-2-10")

        val expectedAst = NumberLiteralExpression("3")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on multiplication expression with 3 operands`() {
        val ast = generateAst("2*2*2")

        val expectedAst = NumberLiteralExpression("8")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on division expression with 3 operands`() {
        val ast = generateAst("8/2/2")

        val expectedAst = NumberLiteralExpression("2")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on expression with unary number literal expression`() {
        val ast = generateAst("-1+2*4")

        val expectedAst = NumberLiteralExpression("7")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on expression with paren exp on left`() {
        val ast = generateAst("(1+2)*4")

        val expectedAst = NumberLiteralExpression("12")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }

    @Test
    fun `performs constants folding on expression with paren exp on right`() {
        val ast = generateAst("12/(1+2)")

        val expectedAst = NumberLiteralExpression("4")
        val foldedAst = ast.expression?.accept(ConstantFoldingVisitor())

        foldedAst.should.equal(expectedAst)
    }
}
