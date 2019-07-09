package com.github.igorperikov.mightywatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.ImportService
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis

object Launcher {
    private val importService = ImportService(RestGithubApiClient(System.getenv("MIGHTY_WATCHER_GITHUB_TOKEN")))

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
        val ms = measureTimeMillis {
            val (languages, labels, ignoredRepos, ignoredIssues) = parseInputParameters()
            val repositories = importService.fetchStarredRepositories(languages, ignoredRepos)
            val issues = ArrayList<Issue>()
            val listOfDeferredIssues = ArrayList<Deferred<List<Issue>>>()
            for (repository in repositories) {
                listOfDeferredIssues += CoroutineScope(newSingleThreadContext("issues-fetcher")).async(
                    block = {
                        importService.fetchIssues(repository, labels)
                    }
                )
            }
            runBlocking {
                listOfDeferredIssues.awaitAll().forEach { issues.addAll(it) }
            }
            issues.removeIf { ignoredIssues.contains(it.htmlUrl) }
            writeResult(
                issues.asSequence()
                    .distinctBy { it.htmlUrl }
                    .sortedByDescending { it.createdAt }
                    .toList()
            )
        }
        println("Import took ${ms}ms")
    }

    private fun writeResult(issues: List<Issue>) {
        File("src/main/resources/result").recreate().appendText(
            issues.joinToString(separator = "\r\n") { it.toString() }
        )
    }

    private fun parseInputParameters(): InputParameters {
        return ObjectMapper(YAMLFactory()).readValue(
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
