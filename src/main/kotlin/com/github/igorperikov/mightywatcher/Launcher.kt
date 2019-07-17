package com.github.igorperikov.mightywatcher

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.ImportService
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

typealias Issues = MutableList<Issue>

object Launcher {
    @JvmStatic
    private val log = LoggerFactory.getLogger(this.javaClass)

    private const val parallelismLevel = 30
    private const val tokenEnvName = "MIGHTY_WATCHER_GITHUB_TOKEN"

    private val importService = ImportService(
        RestGithubApiClient(
            System.getenv(tokenEnvName) ?: throw RuntimeException("$tokenEnvName should be set")
        )
    )

    @JvmStatic
    @ObsoleteCoroutinesApi
    fun main(args: Array<String>) {
        val listOfDeferredIssues = ArrayList<Deferred<Issues>>()
        val rateLimiter = Semaphore(parallelismLevel)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        for ((repository, label) in importService.getSearchTasks()) {
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
                    .toMutableList()
            )
        }
    }

    private fun printResult(issues: Issues) {
        for ((timeGroup, issuesInTimeGroup) in groupByTime(issues)) {
            log.info("{}", timeGroup)
            for (issue in issuesInTimeGroup) {
                log.info("{}", issue)
            }
        }
    }

    private fun groupByTime(issues: Issues): LinkedHashMap<TimeGroup, Issues> {
        val today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
        val yesterday = today.minus(Duration.ofDays(1))
        val thisWeek = today.minus(Duration.ofDays(7))
        val older = Instant.MIN

        val timeGroups = arrayOf(
            TimeGroup(older, "older"),
            TimeGroup(thisWeek, "this week"),
            TimeGroup(yesterday, "yesterday"),
            TimeGroup(today, "today")
        )
        val issuesByTimeGroup = LinkedHashMap<TimeGroup, Issues>()
        for (issue in issues) {
            issuesByTimeGroup.computeIfAbsent(findTimeGroup(timeGroups, issue)) { mutableListOf() }.add(issue)
        }
        return issuesByTimeGroup
    }

    private fun findTimeGroup(timeGroups: Array<TimeGroup>, issue: Issue): TimeGroup {
        val insertionPoint = Arrays.binarySearch(timeGroups, TimeGroup(issue.createdAt))
        return if (insertionPoint >= 0) {
            timeGroups[insertionPoint]
        } else {
            timeGroups[-insertionPoint - 1 - 1]
        }
    }

    private data class TimeGroup(val time: Instant, val name: String = "") : Comparable<TimeGroup> {
        override fun compareTo(other: TimeGroup): Int {
            return time.compareTo(other.time)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TimeGroup

            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun toString(): String {
            return "${name.toUpperCase()}:"
        }
    }
}
