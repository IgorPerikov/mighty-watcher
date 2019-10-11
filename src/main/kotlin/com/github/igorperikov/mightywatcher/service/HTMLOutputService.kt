package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.IssueLine
import com.github.igorperikov.mightywatcher.entity.ResultLine
import com.github.igorperikov.mightywatcher.entity.TimestampLine
import j2html.TagCreator
import j2html.attributes.Attr
import j2html.tags.ContainerTag
import java.io.File
import java.time.LocalDate

class HTMLOutputService : OutputService() {

    override fun outputResults(lines: List<ResultLine>) {
        val outputTags: List<ContainerTag> = lines.map {
            when (it) {
                is TimestampLine -> TagCreator.tr(
                        TagCreator.td(TagCreator.b(it.toString()))
                                .withStyle("text-align:center")
                                .attr(Attr.COLSPAN, 2)
                )
                is IssueLine -> TagCreator.tr(
                        TagCreator.td(it.repoName).withClass("col-xs-4"),
                        TagCreator.td(TagCreator.a(it.title).withHref(it.htmlUrl)).withClass("col-xs-8")
                )
                else -> TagCreator.tr()
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
                                            *outputTags.toTypedArray()
                                    ).withClass("table table-striped")
                            )
                    ).renderFormatted()
            )
        }
    }
}
