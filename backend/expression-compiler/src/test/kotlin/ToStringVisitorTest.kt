import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.generateAst
import org.example.visitors.ToStringVisitor
import org.junit.jupiter.api.Test

class ToStringVisitorTest {
    @Test
    fun `returns the string representation of expression's ast`() {
        val ast = generateAst("(a+c)-2")
        val visitor = ToStringVisitor()
        ast?.accept(visitor)
        visitor.getExpressionString().should.equal("(a+c)-2")
    }
}
