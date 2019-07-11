package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class ImportService(private val githubApiClient: GithubApiClient) {
    fun fetchStarredRepositories(
            includedLanguages: Set<String>,
            excludedLanguages: Set<String>,
            excludedRepositories: Set<String>
    ): List<Repository> {
        return githubApiClient.getStarredRepositories()
                .asSequence()
                .filter { it.hasIssues }
                .filter { it.fullName !in excludedRepositories }
                .filter { it.language?.toLowerCase() !in excludedLanguages }
                .filter { it.language?.toLowerCase() in includedLanguages }
                .toList()
    }

    fun fetchIssues(repoFullName: String, labels: Set<String>): List<Issue> {
        return labels.flatMap { githubApiClient.getIssues(repoFullName, it) }
    }
}
