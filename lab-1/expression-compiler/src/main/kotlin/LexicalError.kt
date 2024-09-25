package org.example

open class LexicalError(override val message: String?, val position: Int) : Exception()
