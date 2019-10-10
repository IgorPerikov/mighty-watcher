package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.entity.XRateResources
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

/**
 * v3 entity api client, full specification - https://developer.entity.com/v3/
 */
class RestGithubApiClient(
        private val httpClient: HttpClient,
        private val callTimeout: Long = 15_000L,
        private val degradeFromRemaining: Long = 100L
) : GithubApiClient {
    private val log = LoggerFactory.getLogger(javaClass)

    private val rate = getXRateLimits().rate
    private val remaining = AtomicLong(rate.remaining)

    private fun <T> runWithinLimits(default: T, block: suspend CoroutineScope.() -> T): T {
        if (remaining.get() > degradeFromRemaining) {
            val result = runBlocking { withTimeout(callTimeout) { block() } }
            val remaining = remaining.decrementAndGet()
            if (remaining <= degradeFromRemaining) {
                val reset = Instant.ofEpochSecond(rate.reset)
                log.warn("GitHub Rest API limit is about to be reached. " +
                        "Until $reset there are $remaining remaining.")
            }
            return result
        }

        return default
    }

    override fun getXRateLimits(): XRateResources {
        return runBlocking {
            withTimeout(callTimeout) {
                httpClient.get<XRateResources> {
                    url {
                        path("rate_limit")
                    }
                }
            }
        }
    }

    override fun getStarredRepositories(): List<Repository> {
        return runWithinLimits(emptyList()) {
            httpClient.get<List<Repository>> {
                url {
                    path("user", "starred")
                    parameter("per_page", "3000")
                }
            }
        }
    }

    override fun getRepositoryLabels(owner: String, repo: String): List<Label> {
        return runWithinLimits(emptyList()) {
            httpClient.get<List<Label>> {
                url {
                    path("repos", owner, repo, "labels")
                    parameter("per_page", "1000")
                }
            }
        }
    }

    override fun getIssues(owner: String, repo: String, label: String, since: String): Issues {
        return runWithinLimits(mutableListOf()) {
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
