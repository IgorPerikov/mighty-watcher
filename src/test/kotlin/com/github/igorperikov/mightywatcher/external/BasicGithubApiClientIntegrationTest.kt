package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.utils.Iso8601Formatter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

private const val mightyWatcherGithubIntegrationTokenName = "MIGHTY_WATCHER_GITHUB_INTEGRATION_TOKEN"

class BasicGithubApiClientIntegrationTest {
    private val githubApiClient: GithubApiClient by lazy {
        BasicGithubApiClient(initHttpClient(System.getenv(mightyWatcherGithubIntegrationTokenName)))
    }

    private fun isIntegrationEnv(): Boolean {
        return System.getenv(mightyWatcherGithubIntegrationTokenName) != null
    }

    private val testRepoOwner = "IgorPerikov"
    private val testRepoName = "github-api-client-integration-testing"
    private val label1Name = "label1"
    private val label2Name = "label2"

    private val startOfCentury = LocalDateTime.of(2000, Month.JANUARY, 1, 1, 1, 1).toInstant(ZoneOffset.UTC)

    @Test
    fun testGetRateLimits() {
        if (isIntegrationEnv()) {
            val xRateLimits = githubApiClient.getRateLimits()
            assertTrue(xRateLimits.remaining >= 0)
            assertTrue(xRateLimits.reset >= startOfCentury.epochSecond)
        }
    }

    @Test
    fun testGetStarredRepositories() {
        if (isIntegrationEnv()) {
            val testRepo = githubApiClient.getStarredRepositories()
                .find { repository -> repository.fullName == "$testRepoOwner/$testRepoName" }
            assertNotNull(testRepo)
        }
    }

    @Test
    fun testGetRepositoryLabels() {
        if (isIntegrationEnv()) {
            val repositoryLabels = githubApiClient.getRepositoryLabels(testRepoOwner, testRepoName)
            assertNotNull(repositoryLabels.find { label -> label.name == label1Name })
            assertNotNull(repositoryLabels.find { label -> label.name == label2Name })
            assertTrue(repositoryLabels.size == 2)
        }
    }

    @Test
    fun `getIssues should not find closed issue`() {
        if (isIntegrationEnv()) {
            val issues = githubApiClient.getIssues(
                testRepoOwner,
                testRepoName,
                label2Name,
                Iso8601Formatter.fromInstant(startOfCentury)
            )
            assertTrue(issues.isEmpty())
            assertNull(issues.find { issue -> issue.title == "closed issue" })
        }
    }

    @Test
    fun `getIssues should not find assigned issue`() {
        if (isIntegrationEnv()) {
            val issues = githubApiClient.getIssues(
                testRepoOwner,
                testRepoName,
                label2Name,
                Iso8601Formatter.fromInstant(startOfCentury)
            )
            assertTrue(issues.isEmpty())
            assertNull(issues.find { issue -> issue.title == "assigned issue" })
        }
    }

    @Test
    fun `getIssues should find open non-assigned issue with given label`() {
        if (isIntegrationEnv()) {
            val issues = githubApiClient.getIssues(
                testRepoOwner,
                testRepoName,
                label1Name,
                Iso8601Formatter.fromInstant(startOfCentury)
            )
            assertTrue(issues.size == 1)
            assertNotNull(issues.find { issue -> issue.title == "non-assigned issue" })
        }
    }
}
