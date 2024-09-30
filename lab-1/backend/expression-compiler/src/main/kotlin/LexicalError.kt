package org.example

class LexicalError(override val message: String?, val position: Int) : Exception()
