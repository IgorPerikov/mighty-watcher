package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.entity.ResultLine
import org.slf4j.LoggerFactory

class ConsoleOutputService : OutputService() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun outputResults(lines: List<ResultLine>) {
        lines.forEach { log.info("{}", it) }
    }

}
