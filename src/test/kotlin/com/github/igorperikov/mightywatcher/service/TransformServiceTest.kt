package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.IssueLine
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import com.github.igorperikov.mightywatcher.entity.TimestampLine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class TransformServiceTest {

    private val today = NamedTimestamp(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), "TODAY")
    private val issue1 = Issue("a/a/a", Instant.now(), Instant.now(), "some issue in repo A")
    private val issue2 = Issue("b/b/b", Instant.now(), Instant.now(), "bad issue in repo B")
    private val issue3 = Issue("c/c/c", Instant.now(), Instant.now(), "good issue in repo C")

    @Test
    fun `test transform`() {
        val service = TransformService()
        val map = LinkedHashMap<NamedTimestamp, Issues>()
        map[today] = mutableListOf(issue1, issue2, issue3)
        val lines = service.transform(map)
        Assertions.assertEquals(4, lines.size)
        Assertions.assertEquals(3, lines.filterIsInstance<IssueLine>().size)
        Assertions.assertEquals(1, lines.filterIsInstance<TimestampLine>().size)
    }
}
