package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.DEFAULT_OUTPUT_TYPE
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.slf4j.LoggerFactory
import java.util.LinkedHashMap


class OutputService(
        private val type: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun getResults(issues: LinkedHashMap<NamedTimestamp, Issues>) {
        when (type) {
            DEFAULT_OUTPUT_TYPE ->
                for ((timeGroupName, issuesInTimeGroup) in issues) {
                    if (issuesInTimeGroup.isEmpty()) continue
                    log.info("{}", timeGroupName)
                    for (issue in issuesInTimeGroup) {
                        log.info(" {}", issue)
                    }
                }
            else ->
                throw NotImplementedError("I didn't do anything new yet")
        }

    }
}
