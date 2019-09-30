package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import com.github.igorperikov.mightywatcher.service.OutputService.Companion.FORMATTER
import com.github.igorperikov.mightywatcher.service.OutputService.Companion.HTML_PATH_FORMAT
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class OutputServiceTest {

    private val today = NamedTimestamp(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), "TODAY")
    private val issue1 = Issue("a/a/a", Instant.now(), Instant.now(), "some issue in repo A")
    private val issue2 = Issue("b/b/b", Instant.now(), Instant.now(), "bad issue in repo B")
    private val issue3 = Issue("c/c/c", Instant.now(), Instant.now(), "good issue in repo C")


    @AfterEach
    fun `remove file`() {
        Files.deleteIfExists(Paths.get(HTML_PATH_FORMAT.format(LocalDate.now().format(FORMATTER))))
    }

    @Test
    fun `html generator`() {
        val outputService = OutputService(HTML_OUTPUT_TYPE)
        val map = LinkedHashMap<NamedTimestamp, Issues>()
        map[today] = mutableListOf(issue1, issue2, issue3)
        outputService.getResults(map)
    }
}
