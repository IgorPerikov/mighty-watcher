package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.RateLimits
import com.github.igorperikov.mightywatcher.entity.Repository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class LimitsAwareGithubApiClient(
    private val delegate: GithubApiClient,
    private val degradeFromRemaining: Long = 100L
) : GithubApiClient {
    private val log = LoggerFactory.getLogger(javaClass)

    private val rate = getRateLimits()
    private val remaining = AtomicLong(rate.remaining)

    private fun <T> runWithinLimits(default: T, block: () -> T): T {
        if (remaining.get() > degradeFromRemaining) {
            val result = block()
            val remaining = remaining.decrementAndGet()
            if (remaining <= degradeFromRemaining) {
                val reset = Instant.ofEpochSecond(rate.reset)
                log.warn(
                    "GitHub Rest API limit is about to be reached. " +
                            "Until $reset there are $remaining remaining."
                )
            }
            return result
        }

        return default
    }

    override fun getRateLimits(): RateLimits {
        return delegate.getRateLimits()
    }

    override fun getStarredRepositories(): List<Repository> {
        return runWithinLimits(
            emptyList(),
            { delegate.getStarredRepositories() }
        )
    }

    override fun getRepositoryLabels(owner: String, repo: String): List<Label> {
        return runWithinLimits(
            emptyList(),
            { delegate.getRepositoryLabels(owner, repo) }
        )
    }

    override fun getIssues(owner: String, repo: String, label: String, since: String): Issues {
        return runWithinLimits(
            mutableListOf(),
            { delegate.getIssues(owner, repo, label, since) }
        )
    }
}
