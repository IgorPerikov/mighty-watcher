package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.SearchTask
import com.github.igorperikov.mightywatcher.utils.Iso8601Formatter
import com.github.igorperikov.mightywatcher.utils.ParallelExecutor
import java.time.Duration
import java.time.Instant

class ImportService(
    private val githubService: GithubService,
    private val labelsService: LabelsService,
    parallelismLevel: Int,
    daysInPast: Long
) {
    private val since = Iso8601Formatter.fromInstant(Instant.now().minus(Duration.ofDays(daysInPast)))
    private val parallelExecutor = ParallelExecutor(parallelismLevel)

    fun findIssues(): Issues {
        return parallelExecutor
            .execute(createSearchTasks()) { task -> githubService.fetchIssues(task.repository, task.label, since) }
            .flatten()
            .distinctBy { it.htmlUrl }
            .toMutableList()
    }

    private fun createSearchTasks(): List<SearchTask> {
        return parallelExecutor
            .execute(githubService.fetchStarredRepositories()) { repository ->
                createSearchTasksForRepo(repository)
            }.flatten()
    }

    private fun createSearchTasksForRepo(repository: Repository): List<SearchTask> {
        return labelsService.findEasyLabelsForRepository(repository).map { label -> SearchTask(repository, label) }
    }
}
