import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.parser.ExpressionStatement
import org.example.parser.IdentifierExpression
import org.example.parser.NumberLiteralExpression
import org.example.parser.Parser
import kotlin.test.Test


class ParserTest {
    @Test
    fun `parses number literal expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "345").tokenize()
        val ast = Parser(tokens=tokens).parse()
        ast.should.equal(ExpressionStatement(expression = NumberLiteralExpression(value = "345")))
    }

    @Test
    fun `parses identifier expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "a").tokenize()
        val ast = Parser(tokens=tokens).parse()
        ast.should.equal(ExpressionStatement(expression = IdentifierExpression(value = "a")))
    }
}
