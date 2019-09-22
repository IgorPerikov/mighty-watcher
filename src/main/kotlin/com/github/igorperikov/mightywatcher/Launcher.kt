package com.github.igorperikov.mightywatcher

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.*
import org.slf4j.LoggerFactory

typealias Issues = MutableList<Issue>

const val INCLUDE_LANG_ENV_NAME = "INCLUDE"
const val EXCLUDE_REPOS_ENV_NAME = "EXCLUDE"
const val DAYS_SINCE_LAST_UPDATE_ENV_NAME = "DAYS"
const val DEFAULT_DAYS_SINCE_LAST_UPDATE = "365"

object Launcher {
    @JvmStatic
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val githubService = GithubService(
        RestGithubApiClient.fromEnv(),
        System.getenv(INCLUDE_LANG_ENV_NAME)?.split(",")?.toHashSet() ?: setOf(),
        System.getenv(EXCLUDE_REPOS_ENV_NAME)?.split(",")?.toHashSet() ?: setOf()
    )
    private val importService = ImportService(
        githubService,
        LabelsService(githubService, EasyLabelsStorage()),
        parallelismLevel = 15,
        daysInPast = (System.getenv(DAYS_SINCE_LAST_UPDATE_ENV_NAME) ?: DEFAULT_DAYS_SINCE_LAST_UPDATE).toLong()
    )
    private val groupingService = GroupingService.withDefaultTimeGroups()

    @JvmStatic
    fun main(args: Array<String>) {
        val issues = importService.findIssues()
        val groupedIssues = groupingService.groupByTime(issues)
        printResult(groupedIssues)
    }

    private fun printResult(issues: LinkedHashMap<NamedTimestamp, Issues>) {
        for ((timeGroupName, issuesInTimeGroup) in issues) {
            if (issuesInTimeGroup.isEmpty()) continue
            log.info("{}", timeGroupName)
            for (issue in issuesInTimeGroup) {
                log.info(" {}", issue)
            }
        }
    }
}
