package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.XRateResources

interface GithubApiClient {
    fun getXRateLimits() : XRateResources

    fun getStarredRepositories(): List<Repository>

    fun getRepositoryLabels(owner: String, repo: String): List<Label>

    /**
     * Will search for non-assigned issues labeled with [label] and updated after [since]
     */
    fun getIssues(owner: String, repo: String, label: String, since: String): Issues
}
