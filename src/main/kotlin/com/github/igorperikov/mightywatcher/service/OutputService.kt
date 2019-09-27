package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.slf4j.LoggerFactory
import java.util.LinkedHashMap
import j2html.TagCreator.*
import j2html.tags.ContainerTag
import javax.xml.parsers.DocumentBuilderFactory

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
                val filteredIssues = issues.filter { it.value.isNotEmpty() }.flatMap { it.value }
                val map: Array<ContainerTag> = filteredIssues.map { issue ->
                    tr(
                            td(issue.getRepoName()).withStyle("border: 1px solid black;"),
                            td(a(issue.title).withHref(issue.htmlUrl)).withStyle("border: 1px solid black;")
                    )
                }.toTypedArray()
                log.info(
                        html(
                                head(
                                        title("Issues report")
                                ),
                                body(
                                        table(
                                                *map
                                        ).withStyle("border: 1px solid black;")
                                )
                        ).renderFormatted()
                )
            }
            else ->
                throw IllegalArgumentException("This output type ${type} is not supported")
        }

    }
}
