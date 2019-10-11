package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.RateLimits
import com.github.igorperikov.mightywatcher.entity.Repository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicLong

class LimitsAwareGithubApiClient(
    private val delegate: GithubApiClient,
    private val degradeStartsFrom: Long = 100L
) : GithubApiClient {
    private val log = LoggerFactory.getLogger(javaClass)

    private val rate = getRateLimits()
    private val remaining = AtomicLong(rate.remaining)

    private fun <T> runWithinLimits(resultSupplier: () -> T, defaultSupplier: () -> T): T {
        if (remaining.get() <= degradeStartsFrom) {
            return defaultSupplier()
        }
        val result = resultSupplier()
        val remaining = remaining.decrementAndGet()
        if (remaining == degradeStartsFrom) {
            val reset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rate.reset), ZoneId.systemDefault())
            log.warn(
                "GitHub Rest API limit is about to be reached. " +
                        "Until $reset there are $remaining remaining."
            )
        }
        return result
    }

    override fun getRateLimits(): RateLimits {
        return delegate.getRateLimits()
    }

    override fun getStarredRepositories(): List<Repository> {
        return runWithinLimits(
            { delegate.getStarredRepositories() },
            { emptyList() }
        )
    }

    override fun getRepositoryLabels(owner: String, repo: String): List<Label> {
        return runWithinLimits(
            { delegate.getRepositoryLabels(owner, repo) },
            { emptyList() }
        )
    }

    override fun getIssues(owner: String, repo: String, label: String, since: String): Issues {
        return runWithinLimits(
            { delegate.getIssues(owner, repo, label, since) },
            { mutableListOf() }
        )
    }
}
