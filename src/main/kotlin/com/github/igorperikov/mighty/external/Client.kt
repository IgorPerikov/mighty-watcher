package com.github.igorperikov.mighty.external

import com.github.igorperikov.mighty.entity.Issue
import com.github.igorperikov.mighty.entity.Repository

interface Client {
    fun getIssues(repoFullName: String, label: String): Set<Issue>

    fun getStarredRepositories(username: String): Set<Repository>
}