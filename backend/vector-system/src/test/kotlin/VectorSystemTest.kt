import org.example.VectorSystem
import org.example.generateAst
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class VectorSystemTest {
    @Test
    fun `processes tasks`() {
        val expression = generateAst("(2+a)-(4*v)")
        assertNotNull(expression)

        val system = VectorSystem(expression = expression)

        system.process()

        println(system.getHistory().joinToString("\n") { it.toString() })
    }

    @Test
    fun `handles concurrent write`() {
        val expression = generateAst("(a*b)*(c*d+a*(b-c))")
        assertNotNull(expression)

        val system = VectorSystem(expression = expression)

        system.process()

//        println(system.getHistory().joinToString("\n") { it.toString() })
    }
}
