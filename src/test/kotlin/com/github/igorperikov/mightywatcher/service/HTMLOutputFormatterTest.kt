package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class HTMLOutputFormatterTest {
    private val today = NamedTimestamp(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), "TODAY")
    private val issue1 = Issue("a/a/a", Instant.now(), Instant.now(), "some issue in repo A")
    private val issue2 = Issue("b/b/b", Instant.now(), Instant.now(), "bad issue in repo B")
    private val issue3 = Issue("c/c/c", Instant.now(), Instant.now(), "good issue in repo C")
    private val bootstrapLine =
        "<link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" rel=\"stylesheet\">"

    @Test
    fun `html generator`() {
        val outputFormatter = HTMLOutputFormatter()
        val transformService = TransformService()
        val map = LinkedHashMap<NamedTimestamp, Issues>()
        map[today] = mutableListOf(issue1, issue2, issue3)
        val resultHtml = outputFormatter.format(transformService.transform(map))
        assertTrue(resultHtml.contains(bootstrapLine))
        assertTrue(resultHtml.contains(issue1.htmlUrl))
        assertTrue(resultHtml.contains(issue2.htmlUrl))
        assertTrue(resultHtml.contains(issue3.htmlUrl))
        assertTrue(resultHtml.contains(issue1.getRepoName()))
        assertTrue(resultHtml.contains(issue2.getRepoName()))
        assertTrue(resultHtml.contains(issue3.getRepoName()))
        assertTrue(resultHtml.contains(issue1.title))
        assertTrue(resultHtml.contains(issue2.title))
        assertTrue(resultHtml.contains(issue3.title))
        assertTrue(resultHtml.contains(today.name))
        assertTrue(resultHtml.contains("Issues report"))
    }
}
