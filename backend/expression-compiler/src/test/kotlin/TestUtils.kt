import org.example.lexicalAnalyzer.LexicalAnalyzerImpl
import org.example.parser.ExpressionStatement
import org.example.parser.Parser

internal fun generateAst(expressionSource: String): ExpressionStatement {
    val tokens = LexicalAnalyzerImpl(expressionSource = expressionSource).tokenize()
    val ast = Parser(tokens = tokens).parse()
    return ast
}
