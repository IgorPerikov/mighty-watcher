package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.slf4j.LoggerFactory
import java.util.LinkedHashMap
import kotlinx.html.*
import kotlinx.html.dom.*

const val PDF_OUTPUT_TYPE = "PDF"
const val HTML_OUTPUT_TYPE = "HTML"
const val CONSOLE_OUTPUT_TYPE = "CONSOLE"

class OutputService(
        private val type: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun getResults(issues: LinkedHashMap<NamedTimestamp, Issues>) {
        when (type) {
            CONSOLE_OUTPUT_TYPE -> {
                for ((timeGroupName, issuesInTimeGroup) in issues) {
                    if (issuesInTimeGroup.isEmpty()) continue
                    log.info("{}", timeGroupName)
                    for (issue in issuesInTimeGroup) {
                        log.info(" {}", issue)
                    }
                }
            }
            PDF_OUTPUT_TYPE -> {
                throw NotImplementedError("PDF export isn't implmented yet")
            }
            HTML_OUTPUT_TYPE -> {
                throw NotImplementedError("HTML export isn't implmented yet")
            }
            else ->
                throw IllegalArgumentException("This output type ${type} is not supported")
        }

    }
}
