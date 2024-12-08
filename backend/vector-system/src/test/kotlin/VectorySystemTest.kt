import kotlinx.coroutines.runBlocking
import org.example.VectorSystem
import org.example.generateAst
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class VectorySystemTest {
    @Test
    fun `processes instructions`() = runBlocking {
        val expression = generateAst("(2*a)-(4*v)")
        assertNotNull(expression)

        val system = VectorSystem(expression = expression)

        system.process()

        println(system.getHistory().joinToString("\n") { it.toString() })
    }
}
