package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository

/**
 * v4 entity api client, full specification - https://developer.entity.com/v4/
 */
class GraphQlGithubApIClient : GithubApiClient {
    override fun getStarredRepositories(username: String): Set<Repository> {
        TODO("not implemented")
    }

    override fun getIssues(repoFullName: String, label: String): Set<Issue> {
        TODO("not implemented")
    }
}
