package com.github.igorperikov.mighty

import com.github.igorperikov.mighty.entity.Issue
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
            val repositories = client.getStarredRepositories(username)
            val languages = mutableSetOf<String>()
            val ignoredRepos = mutableSetOf<String>()

            readResourceFile("languages")
                .lines()
                .forEach { languages.add(it.toLowerCase()) }

            readResourceFile("ignored-repos")
                .lines()
                .forEach { ignoredRepos.add(it) }

            val file = File("src/main/resources/tracked-repos")
            file.delete()
            file.createNewFile()

            repositories
                .filter { it.hasIssues }
                .filter { it.fullName !in ignoredRepos }
                .filter { it.language?.toLowerCase() in languages }
                .forEach { file.appendText(it.fullName + "\r\n") }
        }

        val labelsSet = readResourceFile("labels").lines().filter { it.isNotBlank() }.toSet()
        val ignoredIssues = readResourceFile("ignored-issues").lines().toSet()

        val issues = mutableSetOf<Issue>()

        readResourceFile("tracked-repos")
            .lines()
            .filter { it.isNotBlank() }
            .forEach { repoFullName ->
                labelsSet.forEach { label ->
                    val notIgnoredIssues = client.getIssues(repoFullName, label).filter { it.htmlUrl !in ignoredIssues }
                    issues.addAll(notIgnoredIssues)
                }
            }

        val resultFile = File("src/main/resources/result")
        resultFile.delete()
        resultFile.createNewFile()

        issues
            .sortedByDescending { it.createdAt }
            .forEach {
                resultFile.appendText(it.toString() + "\r\n")
            }
        println("finish at ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
    }

    private fun readResourceFile(name: String): String = Launcher::class.java.classLoader.getResource(name).readText()
}
