package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.slf4j.LoggerFactory
import java.util.LinkedHashMap
import j2html.TagCreator.*
import j2html.attributes.Attr.COLSPAN
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
                val mutableList: MutableList<ContainerTag> = arrayListOf()
                for ((timeGroupName, issuesInTimeGroup) in issues) {
                    if (issuesInTimeGroup.isEmpty()) continue
                    mutableList.add(tr(
                            td(b(timeGroupName.toString())).withStyle("text-align:center").withClass("col-xs-12")
                    ).attr(COLSPAN, 2))
                    for (issue in issuesInTimeGroup) {
                        mutableList.add(tr(
                                td(issue.getRepoName()).withClass("col-xs-4"),
                                td(a(issue.title).withHref(issue.htmlUrl)).withClass("col-xs-8")
                        ))
                    }
                }
                log.info(
                        html(
                                head(
                                        title("Issues report"),
                                        link().withHref("https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css").withRel("stylesheet")
                                ),
                                body(
                                        h3("Issues report").withStyle("text-align: center"),
                                        br(),
                                        table(
                                                *mutableList.toTypedArray()
                                        ).withClass("table table-striped")
                                )
                        ).renderFormatted()
                )
            }
            else ->
                throw IllegalArgumentException("This output type ${type} is not supported")
        }

    }
}
