package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LabelsServiceTest {
    @Test
    fun `labels should be lower case only`() {
        for (easyLabel in EasyLabelsStorage().getEasyLabels()) {
            assertTrue(easyLabel == easyLabel.toLowerCase(), "label '$easyLabel' contains upper case letters")
        }
    }

    @Test
    fun `labels service should fetch appropriate labels ignoring case`() {
        val easyLabelUpperCase = "EASY LABEL"
        val easyLabelLowerCase = easyLabelUpperCase.toLowerCase()

        val githubApiClient = mock<GithubApiClient> {
            on { getRepositoryLabels(any(), any()) } doReturn listOf(Label("hard label"), Label(easyLabelUpperCase))
        }
        val githubService = GithubService(githubApiClient, emptySet(), emptySet())
        val easyLabelsStorage = mock<EasyLabelsStorage> {
            on { getEasyLabels() } doReturn setOf(easyLabelLowerCase)
        }
        val labelsService = LabelsService(githubService, easyLabelsStorage)

        val easyLabelsForRepo = labelsService.findEasyLabelsForRepository(Repository("lang", "owner/repo", true))
        assertTrue(easyLabelsForRepo.size == 1)
        assertEquals(easyLabelLowerCase, easyLabelsForRepo.first())
    }
}
