package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Repository

class LabelsService(
    private val githubService: GithubService,
    private val easyLabelsStorage: EasyLabelsStorage
) {
    fun findEasyLabelsForRepository(repository: Repository): List<String> {
        return githubService
            .getRepositoryLabels(repository)
            .map { it.name.toLowerCase() }
            .filter { easyLabelsStorage.getEasyLabels().contains(it) }
    }
}
