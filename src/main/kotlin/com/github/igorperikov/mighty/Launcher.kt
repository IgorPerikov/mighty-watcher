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
            val languages = getFileContent("languages")
                .map { it.toLowerCase() }
                .toSet()

            val ignoredRepos = getFileContent("ignored-repos")

            val repositories = client.getStarredRepositories(username)
                .filter { it.hasIssues }
                .filter { it.fullName !in ignoredRepos }
                .filter { it.language?.toLowerCase() in languages }

            val file = File("src/main/resources/tracked-repos")
            file.delete()
            file.createNewFile()

            repositories.forEach { file.appendText(it.fullName + "\r\n") }
        }

        val labelsSet = getFileContent("labels")
        val ignoredIssues = getFileContent("ignored-issues")

        val issues = getFileContent("tracked-repos")
            .filter { it.isNotBlank() }
            .flatMap { repoFullName ->
                labelsSet.map { label ->
                    Pair(repoFullName, label)
                }
            }
            .flatMap { client.getIssues(it.first, it.second) }
            .filter { it.htmlUrl !in ignoredIssues }
            .distinctBy { it.htmlUrl }
            .sortedByDescending { it.createdAt }

        val resultFile = File("src/main/resources/result")
        resultFile.delete()
        resultFile.createNewFile()

        issues.forEach {
            resultFile.appendText(it.toString() + "\r\n")
        }

        println("finish at ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
    }

    private fun getFileContent(name: String): Set<String> {
        return Utils.readResourceFile(name)
            .lines()
            .filter { it.isNotBlank() }
            .toSet()
    }
}
