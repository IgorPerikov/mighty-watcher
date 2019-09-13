package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GithubServiceTest {
    private val lang1 = "java"
    private val lang2 = "kotlin"
    private val name1 = "a/a"
    private val name2 = "b/b"
    private val repo1 = Repository(lang1, name1, true)
    private val repo2 = Repository(lang2, name2, true)

    private val githubApiClient = mock<GithubApiClient> {
        on { getStarredRepositories() } doReturn listOf(repo1, repo2)
    }

    @Test
    fun `starred repositories should not contain excluded repositories`() {
        val githubService = GithubService(githubApiClient, emptySet(), setOf(name1))
        val starredRepositories = githubService.fetchStarredRepositories()
        assertEquals(1, starredRepositories.size)
        assertTrue(starredRepositories.contains(repo2))
    }

    @Test
    fun `starred repositories should contain included languages only`() {
        val githubService = GithubService(githubApiClient, setOf(lang1), emptySet())
        val starredRepositories = githubService.fetchStarredRepositories()
        assertEquals(1, starredRepositories.size)
        assertTrue(starredRepositories.contains(repo1))
    }

    @Test
    fun `starred repositories should contain all languages if none was restricted`() {
        val githubService = GithubService(githubApiClient, emptySet(), emptySet())
        val starredRepositories = githubService.fetchStarredRepositories()
        assertEquals(2, starredRepositories.size)
        assertTrue(starredRepositories.contains(repo1))
        assertTrue(starredRepositories.contains(repo2))
    }
}
