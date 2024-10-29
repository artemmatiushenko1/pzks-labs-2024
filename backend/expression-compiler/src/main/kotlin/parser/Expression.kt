package org.example.parser

abstract class Expression {
    abstract fun accept(visitor: Visitor)
}
