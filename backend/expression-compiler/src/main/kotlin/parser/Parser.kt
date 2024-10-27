package org.example.parser

import org.example.Token

/*
  NOTES: 
  1. Generate an AST.
  2. Perform optimisations.
  3. Visitor pattern may be useful for doing optimisations.

  enum class NodeType {
    EXPRESSION_STATEMENT,
    BINARY_EXPRESSION,
    PARENTHESISED_EXPRESSION,
    IDENTIFIER,
    NUMBER_LITERAL,
  }
 */
class Parser {
    fun parse(tokens: List<Token>): ExpressionStatement {
        return ExpressionStatement(expression = NumberLiteral(value = "1"))
    }
}
