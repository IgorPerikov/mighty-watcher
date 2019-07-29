package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.EXCLUDE_REPOS_ENV_NAME
import com.github.igorperikov.mightywatcher.INCLUDE_LANG_ENV_NAME
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.SearchTask
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class ImportService(private val githubApiClient: GithubApiClient) {
    private val easyLabels = listOf(
        "adoptme",
        "contributions welcome",
        "easy",
        "E-easy",
        "E-help-wanted",
        "E-mentor",
        "E-medium",
        "E-needstest",
        "good first issue",
        "good-first-issue",
        "hacktoberfest",
        "help wanted",
        "ideal for contribution",
        "low hanging fruit",
        "noob friendly",
        "PR welcome"
    )

    private val since = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneOffset.UTC)
        .format(Instant.now().minus(Duration.ofDays(365)))

    private val includedLanguages: Set<String> =
        System.getenv(INCLUDE_LANG_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()
    private val excludedRepositories: Set<String> =
        System.getenv(EXCLUDE_REPOS_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()

    fun getSearchTasks(): List<SearchTask> {
        val starredRepositories: List<Repository> = fetchStarredRepositories()
        return starredRepositories
            .flatMap { repository ->
                // TODO: make it parallel + extract to utils, what about `parallelStream()?`
                val repositoryLabels = getRepositoryLabels(repository).map { it.name }.toHashSet()
                easyLabels.filter { repositoryLabels.contains(it) }.map { label -> SearchTask(repository, label) }
            }
    }

    fun fetchIssues(repository: Repository, label: String): Issues {
        return githubApiClient.getIssues(repository.getOwner(), repository.getRepo(), label, since)
    }

    private fun getRepositoryLabels(repository: Repository): List<Label> {
        return githubApiClient.getRepositoryLabels(repository.getOwner(), repository.getRepo())
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
