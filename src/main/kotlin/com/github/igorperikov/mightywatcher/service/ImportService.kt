package com.github.igorperikov.mightywatcher.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.InputParameters
import com.github.igorperikov.mightywatcher.entity.Issue
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
        "E-easy"
    )

    fun getSearchTasks(): List<SearchTask> {
        val inputParameters = parseInputParameters()
        val repositories = fetchStarredRepositories(inputParameters)
        return repositories.flatMap { repository ->
            easyLabels.map { label ->
                SearchTask(
                    repository.fullName,
                    label
                )
            }
        }
    }

    fun fetchIssues(repoFullName: String, label: String): List<Issue> {
        return githubApiClient.getIssues(repoFullName, label)
    }

    private fun fetchStarredRepositories(inputParameters: InputParameters): List<Repository> {
        return githubApiClient.getStarredRepositories()
            .asSequence()
            .filter { it.hasIssues }
            .filter { it.language?.toLowerCase() !in inputParameters.excludedLanguages }
            .filter { it.language?.toLowerCase() in inputParameters.includedLanguages }
            .filter { it.fullName !in inputParameters.excludedRepositories }
            .toList()
    }

    private fun parseInputParameters(): InputParameters {
        val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(
            this::class.java.classLoader.getResource("parameters.json")?.readText()
                ?: throw IllegalArgumentException("parameters.json not found")
        )
    }
}
