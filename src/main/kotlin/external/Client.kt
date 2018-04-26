package external

import entity.Issue
import entity.Repository

interface Client {
    fun getIssues(repoFullName: String, label: String): Set<Issue>

    fun getStarredRepositories(username: String): Set<Repository>
}
