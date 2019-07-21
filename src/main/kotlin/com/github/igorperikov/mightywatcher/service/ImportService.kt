package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.EXCLUDE_REPOS_ENV_NAME
import com.github.igorperikov.mightywatcher.INCLUDE_LANG_ENV_NAME
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.SearchTask
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class ImportService(private val githubApiClient: GithubApiClient) {
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
        "good-first-issue",
        "E-easy",
        "E-help-wanted",
        "E-mentor",
        "E-needstest",
        "E-medium",
        "hacktoberfest"
    )

    private val includedLanguages: Set<String> =
        System.getenv(INCLUDE_LANG_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()
    private val excludedRepositories: Set<String> =
        System.getenv(EXCLUDE_REPOS_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()

    fun getSearchTasks(): List<SearchTask> {
        return fetchStarredRepositories()
            .flatMap { repository ->
                easyLabels.map { label ->
                    SearchTask(
                        repository.fullName,
                        label
                    )
                }
            }
    }

    fun fetchIssues(repoFullName: String, label: String): Issues {
        return githubApiClient.getIssues(repoFullName, label)
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
