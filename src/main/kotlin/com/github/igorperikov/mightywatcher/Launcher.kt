package com.github.igorperikov.mightywatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.ImportService
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlin.system.measureTimeMillis

object Launcher {
    private const val parallelismLevel = 30
    private val importService = ImportService(RestGithubApiClient(System.getenv("MIGHTY_WATCHER_GITHUB_TOKEN")))

    @JvmStatic
    @ObsoleteCoroutinesApi
    fun main(args: Array<String>) {
        val ms = measureTimeMillis {
            val inputParameters = parseInputParameters()
            val listOfDeferredIssues = ArrayList<Deferred<List<Issue>>>()
            val rateLimiter = Semaphore(parallelismLevel)
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            for ((repository, label) in generateTasks(inputParameters)) {
                listOfDeferredIssues += coroutineScope.async(
                        block = {
                            try {
                                rateLimiter.acquire()
                                return@async importService.fetchIssues(repository, label)
                            } finally {
                                rateLimiter.release()
                            }
                        }
                )
            }
            runBlocking {
                printResult(
                        listOfDeferredIssues.awaitAll()
                                .flatten()
                                .asSequence()
                                .distinctBy { it.htmlUrl }
                                .filterNot { inputParameters.excludedIssues.contains(it.htmlUrl) }
                                .sortedByDescending { it.createdAt }
                )
            }
        }
        println("Import took ${ms}ms")
    }

    // TODO: https://github.com/IgorPerikov/mighty-watcher/issues/25
    private fun printResult(issues: Sequence<Issue>) {
        for (issue in issues) {
            println(issue)
        }
    }

    private fun parseInputParameters(): InputParameters {
        return ObjectMapper(YAMLFactory()).readValue(
                this::class.java.classLoader.getResource("parameters.yaml")?.readText()
                        ?: throw IllegalArgumentException("parameters.yaml not found")
        )
    }

    private fun generateTasks(inputParameters: InputParameters): List<SearchTask> {
        val repositories = importService.fetchStarredRepositories(
                inputParameters.includedLanguages,
                inputParameters.excludedLanguages,
                inputParameters.excludedRepositories
        ).filter { it.hasIssues }
        return repositories.flatMap { repository ->
            inputParameters.includedLabels.map { label -> SearchTask(repository.fullName, label) }
        }
    }

    data class SearchTask(val repository: String, val label: String)
}
