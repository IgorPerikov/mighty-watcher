package com.github.igorperikov.mighty

object Utils {
    fun readResourceFile(name: String): String = Launcher::class.java.classLoader.getResource(name).readText()

    fun readToken(): String = readResourceFile("token")
}