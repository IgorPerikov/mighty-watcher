package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.EXCLUDE_REPOS_ENV_NAME
import com.github.igorperikov.mightywatcher.INCLUDE_LANG_ENV_NAME
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.SearchTask
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import com.github.igorperikov.mightywatcher.utils.Iso8601Formatter
import com.github.igorperikov.mightywatcher.utils.ParallelExecutor
import java.time.Duration
import java.time.Instant

class ImportService(private val githubApiClient: GithubApiClient) {
    private val since = Iso8601Formatter.fromInstant(Instant.now().minus(Duration.ofDays(365)))

    private val includedLanguages: Set<String> =
        System.getenv(INCLUDE_LANG_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()

    private val excludedRepositories: Set<String> =
        System.getenv(EXCLUDE_REPOS_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()

    fun getSearchTasks(): List<SearchTask> {
        return ParallelExecutor().execute(fetchStarredRepositories()) { repository ->
            findLabelsConjunction(repository).map { label -> SearchTask(repository, label) }
        }.flatten()
    }

    fun fetchIssues(repository: Repository, label: String): Issues {
        return githubApiClient.getIssues(repository.getOwner(), repository.getRepo(), label, since)
    }

    private fun getRepositoryLabels(repository: Repository): List<Label> {
        return githubApiClient.getRepositoryLabels(repository.getOwner(), repository.getRepo())
    }

    private fun findLabelsConjunction(repository: Repository): List<String> {
        val repositoryLabels = getRepositoryLabels(repository).map { it.name }.toHashSet()
        return LabelsService.getEasyLabels().filter { repositoryLabels.contains(it) }
    }

    private fun fetchStarredRepositories(): List<Repository> {
        return githubApiClient.getStarredRepositories()
            .asSequence()
            .filter { it.hasIssues }
            .filter { languageAllowed(it.language) }
            .filterNot { it.fullName in excludedRepositories }
            .toList()
    }

    private fun languageAllowed(language: String?): Boolean {
        return if (includedLanguages.isEmpty()) {
            true
        } else {
            includedLanguages.contains(language?.toLowerCase())
        }
    }
}
