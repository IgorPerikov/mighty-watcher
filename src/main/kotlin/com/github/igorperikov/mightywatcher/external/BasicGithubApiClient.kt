package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.RateLimits
import com.github.igorperikov.mightywatcher.entity.Repository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * v3 entity api client, full specification - https://developer.entity.com/v3/
 */
class BasicGithubApiClient(
    private val httpClient: HttpClient,
    private val callTimeout: Long = 15_000L
) : GithubApiClient {
    override fun getRateLimits(): RateLimits {
        return runBlocking {
            withTimeout(callTimeout) {
                httpClient.get<RateLimits> {
                    url {
                        path("rate_limit")
                    }
                }
            }
        }
    }

    override fun getStarredRepositories(): List<Repository> {
        return runBlocking {
            withTimeout(callTimeout) {
                httpClient.get<List<Repository>> {
                    url {
                        path("user", "starred")
                        parameter("per_page", "3000")
                    }
                }
            }
        }
    }

    override fun getRepositoryLabels(owner: String, repo: String): List<Label> {
        return runBlocking {
            withTimeout(callTimeout) {
                httpClient.get<List<Label>> {
                    url {
                        path("repos", owner, repo, "labels")
                        parameter("per_page", "1000")
                    }
                }
            }
        }
    }

    override fun getIssues(owner: String, repo: String, label: String, since: String): Issues {
        return runBlocking {
            withTimeout(callTimeout) {
                httpClient.get<Issues> {
                    url {
                        path("repos", owner, repo, "issues")
                        parameter("assignee", "none")
                        parameter("since", since)
                        parameter("labels", label)
                        parameter("sort", "created")
                        parameter("per_page", "2000")
                    }
                }
            }
        }
    }
}
