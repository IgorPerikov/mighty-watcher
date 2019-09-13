package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class GroupingService(private val namedTimestamps: MutableList<NamedTimestamp>) {
    init {
        namedTimestamps.sortByDescending { namedTimestamp -> namedTimestamp.time }
    }

    fun groupByTime(issues: Issues): LinkedHashMap<NamedTimestamp, Issues> {
        issues.sortByDescending { it.createdAt }
        val issuesByNamedTimestamp = LinkedHashMap<NamedTimestamp, Issues>()
        for (namedTimestamp in namedTimestamps) {
            issuesByNamedTimestamp[namedTimestamp] = mutableListOf()
        }
        for (issue in issues) {
            issuesByNamedTimestamp[findSmallestTimestampBiggerThanGiven(issue.createdAt)]?.add(issue)
        }
        return issuesByNamedTimestamp
    }

    private fun findSmallestTimestampBiggerThanGiven(given: Instant): NamedTimestamp {
        return namedTimestamps.find { it.time.isBefore(given) } ?: namedTimestamps.first()
    }

    companion object {
        fun withDefaultTimeGroups(): GroupingService {
            val today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
            val yesterday = today.minus(Duration.ofDays(1))
            val thisWeek = today.minus(Duration.ofDays(7))
            val thisMonth = today.minus(Duration.ofDays(30))
            val older = Instant.MIN

            return GroupingService(
                mutableListOf(
                    NamedTimestamp(today, "TODAY"),
                    NamedTimestamp(yesterday, "YESTERDAY"),
                    NamedTimestamp(thisWeek, "THIS WEEK"),
                    NamedTimestamp(thisMonth, "THIS MONTH"),
                    NamedTimestamp(older, "OLDER")
                )
            )
        }
    }
}
