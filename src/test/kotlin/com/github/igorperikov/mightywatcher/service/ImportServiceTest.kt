package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class ImportServiceTest {
    private val easyLabel1 = "easy1"
    private val easyLabel2 = "easy2"
    private val hardLabel = "hard"

    private val hardIssue = Issue("a/a/a/a", Instant.now(), Instant.now(), "hard")
    private val easyIssue1 = Issue("b/b/b/b", Instant.now(), Instant.now(), "easy1")
    private val easyIssue2 = Issue("c/c/c/c", Instant.now(), Instant.now(), "easy2")

    private val repo1 = Repository("java", "a/a", true)
    private val repo2 = Repository("kotlin", "b/b", true)

    @Test
    fun `should find easy issues only`() {
        val githubService = mock<GithubService> {
            on { fetchStarredRepositories() } doReturn listOf(repo1)
            on { getRepositoryLabels(eq(repo1)) } doReturn listOf(Label(easyLabel1), Label(hardLabel))
            on { fetchIssues(eq(repo1), eq(easyLabel1), any()) } doReturn mutableListOf(easyIssue1)
            on { fetchIssues(eq(repo1), eq(hardLabel), any()) } doReturn mutableListOf(hardIssue)
        }
        val easyLabelsStorage = mock<EasyLabelsStorage> {
            on { getEasyLabels() } doReturn setOf(easyLabel1)
        }
        val labelsService = LabelsService(githubService, easyLabelsStorage)
        val importService = ImportService(githubService, labelsService, 1, 100)

        val issues = importService.findIssues()
        assertEquals(1, issues.size)
        assertEquals(easyIssue1, issues.first())
    }

    @Test
    fun `should find issues from all available repositories`() {
        val githubService = mock<GithubService> {
            on { fetchStarredRepositories() } doReturn listOf(repo1, repo2)
            on { getRepositoryLabels(eq(repo1)) } doReturn listOf(Label(easyLabel1))
            on { getRepositoryLabels(eq(repo2)) } doReturn listOf(Label(easyLabel2))
            on { fetchIssues(eq(repo1), eq(easyLabel1), any()) } doReturn mutableListOf(easyIssue1)
            on { fetchIssues(eq(repo2), eq(easyLabel2), any()) } doReturn mutableListOf(easyIssue2)
        }
        val labelsService = mock<LabelsService> {
            on { findEasyLabelsForRepository(eq(repo1)) } doReturn listOf(easyLabel1)
            on { findEasyLabelsForRepository(eq(repo2)) } doReturn listOf(easyLabel2)
        }
        val importService = ImportService(githubService, labelsService, 1, 42)
        val issues = importService.findIssues()
        assertEquals(2, issues.size)
        assertTrue(issues.contains(easyIssue1))
        assertTrue(issues.contains(easyIssue2))
    }

    @Test
    fun `issue with multiple easy labels should be returned once`() {
        val githubService = mock<GithubService> {
            on { fetchStarredRepositories() } doReturn listOf(repo1)
            on { getRepositoryLabels(eq(repo1)) } doReturn listOf(Label(easyLabel1), Label(easyLabel2))
            on { fetchIssues(eq(repo1), eq(easyLabel1), any()) } doReturn mutableListOf(easyIssue1)
            on { fetchIssues(eq(repo1), eq(easyLabel2), any()) } doReturn mutableListOf(easyIssue1)
        }
        val labelsService = mock<LabelsService> {
            on { findEasyLabelsForRepository(eq(repo1)) } doReturn listOf(easyLabel1, easyLabel2)
        }

        val importService = ImportService(githubService, labelsService, 1, 100)
        val issues = importService.findIssues()
        assertEquals(1, issues.size)
        assertEquals(easyIssue1, issues.first())
    }
}
