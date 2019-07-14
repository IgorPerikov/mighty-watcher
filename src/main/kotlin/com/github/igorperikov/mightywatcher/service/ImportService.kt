package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class ImportService(private val githubApiClient: GithubApiClient) {
    fun fetchStarredRepositories(inputParameters: InputParameters): List<Repository> {
        return githubApiClient.getStarredRepositories()
                .asSequence()
                .filter { it.hasIssues }
                .filter { it.language?.toLowerCase() !in inputParameters.excludedLanguages }
                .filter { it.language?.toLowerCase() in inputParameters.includedLanguages }
                .filter { it.fullName !in inputParameters.excludedRepositories }
                .toList()
    }

    fun fetchIssues(repoFullName: String, label: String): List<Issue> {
        return githubApiClient.getIssues(repoFullName, label)
    }
}
