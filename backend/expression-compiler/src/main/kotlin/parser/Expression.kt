package org.example.parser

import org.example.parser.visitors.Visitor

abstract class Expression {
    abstract fun accept(visitor: Visitor)
}
