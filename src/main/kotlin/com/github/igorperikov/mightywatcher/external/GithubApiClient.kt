package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository

interface GithubApiClient {
    fun getStarredRepositories(): List<Repository>

    fun getIssues(repoFullName: String, label: String): List<Issue>
}
