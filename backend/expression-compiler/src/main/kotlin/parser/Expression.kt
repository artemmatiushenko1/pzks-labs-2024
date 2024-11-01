package org.example.parser

import org.example.visitors.Visitor

abstract class Expression {
    abstract fun accept(visitor: Visitor): Expression
}
