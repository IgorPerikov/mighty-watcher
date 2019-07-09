package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class ImportService(private val githubApiClient: GithubApiClient) {
    fun fetchStarredRepositories(
        languages: Set<String>,
        ignoredRepos: Set<String>
    ): List<Repository> {
        return githubApiClient.getStarredRepositories()
            .asSequence()
            .filter { it.hasIssues }
            .filter { it.fullName !in ignoredRepos }
            .filter { it.language?.toLowerCase() in languages }
            .toList()
    }

    fun fetchIssues(
        repository: Repository,
        labels: Set<String>
    ): List<Issue> {
        return labels.flatMap { githubApiClient.getIssues(repository.fullName, it) }
    }
}
