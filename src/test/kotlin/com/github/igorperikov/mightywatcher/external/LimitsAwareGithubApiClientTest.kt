package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.RateLimits
import com.github.igorperikov.mightywatcher.entity.Repository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class LimitsAwareGithubApiClientTest {
    @Test
    fun `client should degrade if limits are being hit`() {
        val degradeLimit = 10L
        val githubApiClientDelegate = mock<GithubApiClient> {
            on { getRateLimits() } doReturn RateLimits(degradeLimit - 1, 42L)
            on { getRepositoryLabels(any(), any()) } doReturn listOf(Label("label name"))
            on { getStarredRepositories() } doReturn listOf(Repository("", "", true))
            on { getIssues(any(), any(), any(), any()) } doReturn mutableListOf(
                Issue("", Instant.now(), Instant.now(), "")
            )
        }
        val client = LimitsAwareGithubApiClient(githubApiClientDelegate, degradeLimit)
        assertTrue(client.getStarredRepositories().isEmpty())
        assertTrue(client.getRepositoryLabels("", "").isEmpty())
        assertTrue(client.getIssues("", "", "", "").isEmpty())
    }

    @Test
    fun `client should not degrade if limits are not being hit`() {
        val degradeLimit = 10L
        val safeLimit = degradeLimit + 10
        val githubApiClientDelegate = mock<GithubApiClient> {
            on { getRateLimits() } doReturn RateLimits(safeLimit, 42L)
            on { getRepositoryLabels(any(), any()) } doReturn listOf(Label("label name"))
            on { getStarredRepositories() } doReturn listOf(Repository("", "", true))
            on { getIssues(any(), any(), any(), any()) } doReturn mutableListOf(
                Issue("", Instant.now(), Instant.now(), "")
            )
        }
        val client = LimitsAwareGithubApiClient(githubApiClientDelegate, degradeLimit)
        assertTrue(client.getStarredRepositories().isNotEmpty())
        assertTrue(client.getRepositoryLabels("", "").isNotEmpty())
        assertTrue(client.getIssues("", "", "", "").isNotEmpty())
    }
}
