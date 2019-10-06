package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import java.time.format.DateTimeFormatter
import java.util.*

abstract class OutputService {
    companion object {

        fun createOutputService(type: String): OutputService {
            return when (type) {
                HTML_OUTPUT_TYPE -> HTMLOutputService()
                CONSOLE_OUTPUT_TYPE -> ConsoleOutputService()
                else -> throw IllegalArgumentException("No supported output service of $type exists§")
            }
        }

        const val HTML_PATH_FORMAT = "might-watch-report-%s.html"
        const val PDF_OUTPUT_TYPE = "PDF"
        const val HTML_OUTPUT_TYPE = "HTML"
        const val CONSOLE_OUTPUT_TYPE = "CONSOLE"
        @JvmField
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
    }

    abstract fun getResults(issues: LinkedHashMap<NamedTimestamp, Issues>)

}
