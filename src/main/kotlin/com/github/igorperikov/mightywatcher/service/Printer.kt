package com.github.igorperikov.mightywatcher.service

import java.io.PrintStream

class Printer(private val printStream: PrintStream) {
    fun write(text: String) {
        printStream.print(text)
    }
}
