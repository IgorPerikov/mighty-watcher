package com.github.igorperikov.mightywatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.ImportService
import kotlinx.coroutines.experimental.*
import java.io.File
import kotlin.system.measureTimeMillis

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
object Launcher {
    private val importService = ImportService(RestGithubApiClient(System.getenv("GITHUB_TOKEN")))

    @JvmStatic
    fun main(args: Array<String>) {
        val ms = measureTimeMillis {
            val (username, languages, labels, ignoredRepos, ignoredIssues) = parseInputParameters()
            val repositories = importService.fetchStarredRepositories(username, languages, ignoredRepos)
            val issues = ArrayList<Issue>()
            runBlocking {
                val listOfDeferredIssues = ArrayList<Deferred<List<Issue>>>()
                for (repository in repositories) {
                    async(context = newSingleThreadContext("issues-fetcher")) {
                        importService.fetchIssues(repository, labels)
                    }.also { listOfDeferredIssues.add(it) }
                }
                listOfDeferredIssues.awaitAll().forEach { issues.addAll(it) }
            }
            issues.removeIf { ignoredIssues.contains(it.htmlUrl) }
            writeResult(issues.distinctBy { it.htmlUrl }.sortedByDescending { it.createdAt })
        }
        println("Import took ${ms}ms")
    }

    private fun writeResult(issues: List<Issue>) {
        File("src/main/resources/result").recreate().appendText(
            issues.joinToString(separator = "\r\n") { it.toString() }
        )
    }

    private fun parseInputParameters(): InputParameters {
        val objectMapper = ObjectMapper(YAMLFactory())
        return objectMapper.readValue(
            this::class.java.classLoader.getResource("parameters.yaml")?.readText()
                ?: throw IllegalArgumentException("parameters.yaml not found")
        )
    }
}

internal fun File.recreate(): File {
    delete()
    createNewFile()
    return this
}
