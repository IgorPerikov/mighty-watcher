package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class GroupingServiceTest {
    private val today = NamedTimestamp(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), "TODAY")
    private val yesterday = NamedTimestamp(today.time.minus(Duration.ofDays(1)), "YESTERDAY")
    private val thisWeek = NamedTimestamp(today.time.minus(Duration.ofDays(7)), "THIS WEEK")
    private val older = NamedTimestamp(Instant.MIN, "OLDER")
    private val namedTimestamps = mutableListOf(today, yesterday, thisWeek, older)
    private val groupingService = GroupingService(namedTimestamps)
    private val issue1 = Issue("a/a/a", today.time.plusSeconds(1), Instant.now(), "")
    private val issue2 = Issue("b/b/b", today.time.minusSeconds(1), Instant.now(), "")
    private val issue3 = Issue("c/c/c", yesterday.time.plusSeconds(1), Instant.now(), "")
    private val issue4 = Issue("d/d/d", yesterday.time.minusSeconds(1), Instant.now(), "")
    private val issue5 = Issue("e/e/e", thisWeek.time.plusSeconds(1), Instant.now(), "")
    private val issue6 = Issue("f/f/f", thisWeek.time.minusSeconds(1), Instant.now(), "")
    private val issue7 = Issue("g/g/g", older.time.plusSeconds(1), Instant.now(), "")
    private val issues = mutableListOf(issue1, issue2, issue3, issue4, issue5, issue6, issue7)

    @Test
    fun `complex use case`() {
        val issuesGroupedByTime = groupingService.groupByTime(issues)

        val todayIssues = issuesGroupedByTime[today]!!
        assertTrue(todayIssues.size == 1)
        assertEquals(issue1, todayIssues.first())

        val yesterdayIssues = issuesGroupedByTime[yesterday]!!
        assertTrue(yesterdayIssues.size == 2)
        assertEquals(issue2, yesterdayIssues.first())
        assertEquals(issue3, yesterdayIssues[1])

        val thisWeekIssues = issuesGroupedByTime[thisWeek]!!
        assertTrue(thisWeekIssues.size == 2)
        assertEquals(issue4, thisWeekIssues.first())
        assertEquals(issue5, thisWeekIssues[1])

        val olderIssues = issuesGroupedByTime[older]!!
        assertTrue(olderIssues.size == 2)
        assertEquals(issue6, olderIssues.first())
        assertEquals(issue7, olderIssues[1])
    }

    @Test
    fun `named timestamps can be passed with no particular order to constructor`() {
        for (i in 0..10) {
            assertEquals(
                groupingService.groupByTime(issues),
                GroupingService(namedTimestamps.shuffled().toMutableList()).groupByTime(issues)
            )
        }
    }

    @Test
    fun `issues can be passed to service with no particular order`() {
        for (i in 0..10) {
            assertEquals(
                groupingService.groupByTime(issues),
                groupingService.groupByTime(issues.shuffled().toMutableList())
            )
        }
    }
}
