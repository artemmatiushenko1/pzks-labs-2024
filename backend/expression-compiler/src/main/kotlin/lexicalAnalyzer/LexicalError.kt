package org.example.lexicalAnalyzer

class LexicalError(override val message: String?, val position: Int) : Exception()
