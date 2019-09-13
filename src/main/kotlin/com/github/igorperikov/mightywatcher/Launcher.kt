package com.github.igorperikov.mightywatcher

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.*
import org.slf4j.LoggerFactory

typealias Issues = MutableList<Issue>

const val INCLUDE_LANG_ENV_NAME = "INCLUDE"
const val EXCLUDE_REPOS_ENV_NAME = "EXCLUDE"

object Launcher {
    @JvmStatic
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val githubService = GithubService(
        RestGithubApiClient.fromEnv(),
        System.getenv(INCLUDE_LANG_ENV_NAME)?.split(",")?.toHashSet() ?: setOf(),
        System.getenv(EXCLUDE_REPOS_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()
    )
    private val importService = ImportService(githubService, LabelsService(githubService, EasyLabelsStorage()), 365)
    private val groupingService = GroupingService.withDefaultTimeGroups()

    @JvmStatic
    fun main(args: Array<String>) {
        printResult(importService.findIssues())
    }

    private fun printResult(issues: Issues) {
        for ((timeGroupName, issuesInTimeGroup) in groupingService.groupByTime(issues)) {
            if (issuesInTimeGroup.isEmpty()) continue
            log.info("{}", timeGroupName)
            for (issue in issuesInTimeGroup) {
                log.info(" {}", issue)
            }
        }
    }
}
