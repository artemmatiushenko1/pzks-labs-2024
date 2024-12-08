import org.example.ExpressionCompiler
import org.example.ToTasksVisitor
import org.junit.jupiter.api.Test

class ToTasksTreeVisitorTest {
    @Test
    fun `returns tasks`() {
        val visitor = ToTasksVisitor()
        val expression = ExpressionCompiler().produceOptimizedAst("(a*b)*(c*d+a*(b-c))")
        expression?.accept(visitor)

        println(visitor.getTasks())
    }
}
