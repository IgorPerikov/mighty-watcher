package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class ImportService(private val githubApiClient: GithubApiClient) {
    fun fetchStarredRepositories(
        username: String,
        languages: Set<String>,
        ignoredRepos: Set<String>
    ): List<Repository> {
        return githubApiClient.getStarredRepositories(username)
            .filter { it.hasIssues }
            .filter { it.fullName !in ignoredRepos }
            .filter { it.language?.toLowerCase() in languages }
    }

    fun fetchIssues(
        repository: Repository,
        labels: Set<String>
    ): List<Issue> {
        return labels.map { githubApiClient.getIssues(repository.fullName, it) }.flatMap { it }
    }
}
