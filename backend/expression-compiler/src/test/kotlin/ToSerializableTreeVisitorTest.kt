import net.oddpoet.expect.extension.equal
import net.oddpoet.expect.should
import org.example.LexicalAnalyzerImpl
import org.example.TreeNode
import org.example.parser.Parser
import org.example.parser.ToSerializableTreeVisitor
import kotlin.test.Test
import kotlin.test.assertNotNull

class ToSerializableTreeVisitorTest {
    @Test
    fun `produces tree for number literal expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "1").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(TreeNode(value = "1"))

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for identifier expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "abc").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(TreeNode(value = "abc"))

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for binary expression with 2 operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "3+5").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(
            TreeNode(value = "+")
                .addNode(TreeNode(value = "3"))
                .addNode(TreeNode(value = "5"))
        )

        println(actualTree.children)

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for binary expression with 3 operands`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "3+5*1").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(
            TreeNode(value = "+")
                .addNode(TreeNode(value = "3"))
                .addNode(
                    TreeNode(value = "*")
                        .addNode(TreeNode(value = "5"))
                        .addNode(TreeNode(value = "1"))
                )
        )

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for unary number literal expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "-1").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(
            TreeNode(value = "-")
                .addNode(TreeNode(value = "0"))
                .addNode(TreeNode(value = "1"))
        )

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for paren binary expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(2+a)").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        expectedTree.addNode(
            TreeNode(value = "+")
                .addNode(TreeNode(value = "2"))
                .addNode(TreeNode(value = "a"))
        )

        actualTree.should.equal(expectedTree)
    }

    @Test
    fun `produces tree for expression`() {
        val tokens = LexicalAnalyzerImpl(expressionSource = "(2+a)*((-2/4))-7").tokenize()
        val ast = Parser(tokens = tokens).parse()

        val visitor = ToSerializableTreeVisitor()
        assertNotNull(ast.expression)
        ast.expression!!.accept(visitor)

        val actualTree = visitor.getTree()

        val expectedTree = TreeNode(value = null)
        val rootBinaryNode = TreeNode(value = "-")
        rootBinaryNode
            .addNode(
                TreeNode(value = "*")
                    .addNode(
                        TreeNode(value = "+")
                            .addNode(TreeNode(value = "2"))
                            .addNode(TreeNode(value = "a"))
                    )
                    .addNode(
                        TreeNode(value = "/")
                            .addNode(
                                TreeNode(value = "-")
                                    .addNode(TreeNode(value = "0"))
                                    .addNode(TreeNode(value = "2"))
                            )
                            .addNode(TreeNode(value = "4"))
                    )
            )
            .addNode(TreeNode(value = "7"))

        expectedTree.addNode(rootBinaryNode)

        actualTree.should.equal(expectedTree)
    }
}
