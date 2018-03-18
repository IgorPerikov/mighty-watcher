package com.github.igorperikov.mighty

object Utils {
    fun readResourceFile(name: String): String = Utils::class.java.classLoader.getResource(name).readText()
}
