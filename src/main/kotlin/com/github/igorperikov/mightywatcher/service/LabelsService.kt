package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient

class LabelsService(
    private val githubApiClient: GithubApiClient,
    private val easyLabelsStorage: EasyLabelsStorage
) {
    fun findEasyLabelsForRepository(repository: Repository): List<String> {
        val repositoryLabels = getRepositoryLabels(repository).map { it.name.toLowerCase() }.toList()
        return repositoryLabels.filter { easyLabelsStorage.getEasyLabels().contains(it) }
    }

    private fun getRepositoryLabels(repository: Repository): List<Label> {
        return githubApiClient.getRepositoryLabels(repository.getOwner(), repository.getRepo())
    }
}
