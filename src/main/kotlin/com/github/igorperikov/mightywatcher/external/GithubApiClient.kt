package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository

interface GithubApiClient {
    fun getStarredRepositories(): Set<Repository>

    fun getIssues(repoFullName: String, label: String): Set<Issue>
}
