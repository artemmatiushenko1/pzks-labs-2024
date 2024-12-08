import org.example.VectorSystem
import org.example.generateAst
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class VectorySystemTest {
    @Test
    fun `processes instructions`() {
        val expression = generateAst("(2+a)-(4*v)")
        assertNotNull(expression)

        val system = VectorSystem(expression = expression)

        system.process()

        println(system.getHistory().joinToString("\n") { it.toString() })
    }

    @Test
    fun `handles cocurrent write`() {
        val expression = generateAst("(a*b)*(c*d+a*(b-c))")
        assertNotNull(expression)

        val system = VectorSystem(expression = expression)

        system.process()

        println(system.getHistory().joinToString("\n") { it.toString() })
    }
}
