package com.github.igorperikov.mightywatcher

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.service.ImportService
import java.io.File
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier

object Launcher {
    private val importService = ImportService()
    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    @JvmStatic
    fun main(args: Array<String>) {
        println("start ${Instant.now()}")
        val (username, languages, ignoredRepos, labels, ignoredIssues) = parseInputParameters()
        val repositories = importService.fetchStarredRepositories(username, languages, ignoredRepos)
        val issues = ArrayList<Issue>()
        val cfs = ArrayList<CompletableFuture<List<Issue>>>()
        for (repository in repositories) {
            CompletableFuture.supplyAsync(
                Supplier { importService.fetchIssues(repository, labels) },
                executor
            ).also { cfs.add(it) }
        }
        CompletableFuture.allOf(*cfs.toTypedArray()).get()
        cfs.map { it.get() }.forEach { issues.addAll(it) }
        issues.removeIf { ignoredIssues.contains(it.title) }
        writeResult(issues.distinctBy { it.htmlUrl }.sortedByDescending { it.createdAt })
        println("end ${Instant.now()}")
        executor.shutdownNow()
    }

    private fun writeResult(issues: List<Issue>) {
        File("src/main/resources/result").recreate().appendText(
            issues.joinToString(separator = "\r\n") { it.toString() }
        )
    }

    private fun parseInputParameters(): InputParameters {
        return InputParameters(
            readResourceFile("username"),
            readFileLines("languages"),
            readFileLines("ignored-repos"),
            readFileLines("labels"),
            readFileLines("ignored-issues")
        )
    }

    private fun readResourceFile(filename: String): String =
        this::class.java.classLoader.getResource(filename)?.readText()
            ?: throw IllegalArgumentException("File $filename doesn't exist")

    private fun readFileLines(filename: String): Set<String> = readResourceFile(filename)
        .lines()
        .filter { it.isNotBlank() }
        .toSet()

    private data class InputParameters(
        val username: String,
        val languages: Set<String>,
        val ignoredRepos: Set<String>,
        val labels: Set<String>,
        val ignoredIssues: Set<String>
    )
}

internal fun File.recreate(): File {
    delete()
    createNewFile()
    return this
}
