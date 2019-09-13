package com.github.igorperikov.mightywatcher.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class Iso8601FormatterTest {
    @Test
    fun `conversion follows ISO8601 format`() {
        val localDateTime = LocalDateTime.of(2019, Month.SEPTEMBER, 10, 19, 57, 10)
        val formattedString = Iso8601Formatter.fromInstant(localDateTime.toInstant(ZoneOffset.UTC))
        assertEquals("2019-09-10T19:57:10", formattedString)
    }
}
