package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository

interface GithubApiClient {
    fun getIssues(repoFullName: String, label: String): Set<Issue>

    fun getStarredRepositories(username: String): Set<Repository>
}
