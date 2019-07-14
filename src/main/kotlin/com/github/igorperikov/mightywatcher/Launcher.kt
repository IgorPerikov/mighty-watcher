package com.github.igorperikov.mightywatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.ImportService
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import org.slf4j.LoggerFactory

object Launcher {
    @JvmStatic
    private val log = LoggerFactory.getLogger(this.javaClass)

    private const val parallelismLevel = 30
    private const val tokenEnvName = "MIGHTY_WATCHER_GITHUB_TOKEN"

    private val easyLabels = listOf(
            "adoptme",
            "contributions welcome",
            "help wanted",
            "good first issue",
            "PR welcome",
            "noob friendly",
            "ideal for contribution",
            "low hanging fruit",
            "easy",
            "E-easy"
    )
    private val importService = ImportService(
            RestGithubApiClient(
                    System.getenv(tokenEnvName) ?: throw RuntimeException("$tokenEnvName should be set")
            )
    )

    @JvmStatic
    @ObsoleteCoroutinesApi
    fun main(args: Array<String>) {
        val listOfDeferredIssues = ArrayList<Deferred<List<Issue>>>()
        val rateLimiter = Semaphore(parallelismLevel)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        for ((repository, label) in generateTasks()) {
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
                            .sortedByDescending { it.createdAt }
            )
        }
    }

    // TODO: https://github.com/IgorPerikov/mighty-watcher/issues/25
    private fun printResult(issues: Sequence<Issue>) {
        for (issue in issues) {
            log.info(issue.toString())
        }
    }

    private fun parseInputParameters(): InputParameters {
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(
                this::class.java.classLoader.getResource("parameters.json")?.readText()
                        ?: throw IllegalArgumentException("parameters.json not found")
        )
    }

    private fun generateTasks(): List<SearchTask> {
        val inputParameters = parseInputParameters()
        val repositories = importService.fetchStarredRepositories(inputParameters)
        return repositories.flatMap { repository -> easyLabels.map { label -> SearchTask(repository.fullName, label) } }
    }

    data class SearchTask(val repository: String, val label: String)
}
