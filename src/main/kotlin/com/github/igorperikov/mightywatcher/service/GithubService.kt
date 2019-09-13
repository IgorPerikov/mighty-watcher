package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class GithubService(
    private val githubApiClient: GithubApiClient,
    private val includedLanguages: Set<String>,
    private val excludedRepositories: Set<String>
) {
    fun fetchStarredRepositories(): List<Repository> {
        return githubApiClient.getStarredRepositories()
            .asSequence()
            .filter { it.hasIssues }
            .filter { languageAllowed(it.language) }
            .filterNot { it.fullName in excludedRepositories }
            .toList()
    }

    fun fetchIssues(repository: Repository, label: String, since: String): Issues {
        return githubApiClient.getIssues(repository.getOwner(), repository.getRepo(), label, since)
    }

    fun getRepositoryLabels(repository: Repository): List<Label> {
        return githubApiClient.getRepositoryLabels(repository.getOwner(), repository.getRepo())
    }

    private fun languageAllowed(language: String?): Boolean {
        return if (includedLanguages.isEmpty()) {
            true
        } else {
            includedLanguages.contains(language?.toLowerCase())
        }
    }
}
