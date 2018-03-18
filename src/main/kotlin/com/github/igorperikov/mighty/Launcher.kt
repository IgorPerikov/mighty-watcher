package com.github.igorperikov.mighty

import com.github.igorperikov.mighty.external.Client
import com.github.igorperikov.mighty.external.RestClient
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime

object Launcher {
    private val importRepositories = (System.getProperty("import.repositories") ?: "false").toBoolean()

    private val client: Client = RestClient()

    private val username: String by lazy {
        Utils.readResourceFile("username")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("start at  ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
        if (importRepositories) {
            val languages = readResourceFile("languages")
                .lines()
                .map { it.toLowerCase() }
                .toSet()

            val ignoredRepos = readResourceFile("ignored-repos").lines().toSet()

            val repositories = client.getStarredRepositories(username)
                .filter { it.hasIssues }
                .filter { it.fullName !in ignoredRepos }
                .filter { it.language?.toLowerCase() in languages }

            val file = File("src/main/resources/tracked-repos")
            file.delete()
            file.createNewFile()

            repositories.forEach { file.appendText(it.fullName + "\r\n") }
        }

        val labelsSet = readResourceFile("labels").lines().filter { it.isNotBlank() }.toSet()
        val ignoredIssues = readResourceFile("ignored-issues").lines().toSet()

        val issues = readResourceFile("tracked-repos")
            .lines()
            .filter { it.isNotBlank() }
            .flatMap { repoFullName ->
                labelsSet.map { label ->
                    Pair(repoFullName, label)
                }
            }
            .flatMap { client.getIssues(it.first, it.second) }
            .filter { it.htmlUrl !in ignoredIssues }
            .sortedByDescending { it.createdAt }

        val resultFile = File("src/main/resources/result")
        resultFile.delete()
        resultFile.createNewFile()

        issues.forEach {
            resultFile.appendText(it.toString() + "\r\n")
        }

        println("finish at ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
    }

    private fun readResourceFile(name: String): String = Launcher::class.java.classLoader.getResource(name).readText()
}
