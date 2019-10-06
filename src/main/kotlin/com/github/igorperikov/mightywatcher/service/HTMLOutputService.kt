package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import j2html.TagCreator
import j2html.attributes.Attr
import j2html.tags.ContainerTag
import java.io.File
import java.time.LocalDate
import java.util.*

class HTMLOutputService : OutputService() {

    override fun getResults(issues: LinkedHashMap<NamedTimestamp, Issues>) {
        val mutableList: MutableList<ContainerTag> = arrayListOf()
        for ((timeGroupName, issuesInTimeGroup) in issues) {
            if (issuesInTimeGroup.isEmpty()) continue
            mutableList.add(TagCreator.tr(
                    TagCreator.td(TagCreator.b(timeGroupName.toString()))
                            .withStyle("text-align:center")
                            .attr(Attr.COLSPAN, 2)
            ))
            for (issue in issuesInTimeGroup) {
                mutableList.add(TagCreator.tr(
                        TagCreator.td(issue.getRepoName()).withClass("col-xs-4"),
                        TagCreator.td(TagCreator.a(issue.title).withHref(issue.htmlUrl)).withClass("col-xs-8")
                ))
            }
        }

        File(HTML_PATH_FORMAT.format(LocalDate.now().format(FORMATTER))).printWriter().use { out ->
            out.println(
                    TagCreator.html(
                            TagCreator.head(
                                    TagCreator.title("Issues report"),
                                    TagCreator.link().withHref("https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css").withRel("stylesheet")
                            ),
                            TagCreator.body(
                                    TagCreator.h3("Issues report").withStyle("text-align: center"),
                                    TagCreator.br(),
                                    TagCreator.table(
                                            *mutableList.toTypedArray()
                                    ).withClass("table table-striped")
                            )
                    ).renderFormatted()
            )
        }
    }

}