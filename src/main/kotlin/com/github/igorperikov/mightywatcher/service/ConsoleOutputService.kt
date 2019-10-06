package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import org.slf4j.LoggerFactory
import java.util.*

class ConsoleOutputService : OutputService() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getResults(issues: LinkedHashMap<NamedTimestamp, Issues>) {
        for ((timeGroupName, issuesInTimeGroup) in issues) {
            if (issuesInTimeGroup.isEmpty()) continue
            log.info("{}", timeGroupName)
            for (issue in issuesInTimeGroup) {
                log.info(" {}", issue)
            }
        }
    }

}
