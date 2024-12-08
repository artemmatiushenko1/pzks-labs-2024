import org.example.ExpressionCompiler
import org.example.ToInstructionsVisitor
import org.example.generateAst
import org.junit.jupiter.api.Test

class ToInstructionsTreeVisitorTest {
    @Test
    fun `returns instructions`() {
        val visitor = ToInstructionsVisitor()
        val expression = ExpressionCompiler().produceOptimizedAst("a+b+c+d+e+f+k+l")
        expression?.accept(visitor)

        println(visitor.getInstructions())
    }
}
