package com.github.igorperikov.mightywatcher.utils

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Iso8601Formatter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneOffset.UTC)

    fun fromInstant(instant: Instant): String {
        return formatter.format(instant)
    }
}
