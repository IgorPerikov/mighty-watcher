package com.github.igorperikov.mightywatcher

import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.external.BasicGithubApiClient
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import com.github.igorperikov.mightywatcher.external.LimitsAwareGithubApiClient
import com.github.igorperikov.mightywatcher.external.initHttpClient
import com.github.igorperikov.mightywatcher.service.*
import org.koin.core.context.startKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module

typealias Issues = MutableList<Issue>

const val INCLUDE_LANG_ENV_NAME = "INCLUDE"
const val EXCLUDE_REPOS_ENV_NAME = "EXCLUDE"
const val PARALLELISM_LEVEL_ENV_NAME = "PARALLELISM"
const val DEFAULT_PARALLELISM_LEVEL = 10
const val DAYS_SINCE_LAST_UPDATE_ENV_NAME = "DAYS"
const val DEFAULT_DAYS_SINCE_LAST_UPDATE = 90L
const val TOKEN_ENV_NAME = "TOKEN"

object Launcher {
    private val applicationModule = module {
        single { initHttpClient(getProperty(TOKEN_ENV_NAME)) }
        single { LimitsAwareGithubApiClient(BasicGithubApiClient(get())) as GithubApiClient }
        single { GithubService(get(), getSetProperty(INCLUDE_LANG_ENV_NAME), getSetProperty(EXCLUDE_REPOS_ENV_NAME)) }
        single { EasyLabelsStorage() }
        single { LabelsService(get(), get()) }
        single {
            ImportService(
                get(),
                get(),
                getProperty(PARALLELISM_LEVEL_ENV_NAME, DEFAULT_PARALLELISM_LEVEL),
                getProperty(DAYS_SINCE_LAST_UPDATE_ENV_NAME, DEFAULT_DAYS_SINCE_LAST_UPDATE)
            )
        }
        single { GroupingService.withDefaultTimeGroups() }
        single { PlainTextOutputFormatter() as OutputFormatter }
        single { TransformService() }
        single { Printer(System.out) }
    }

    private fun Scope.getSetProperty(key: String): Set<String> = getPropertyOrNull<String>(key)
        ?.split(",")?.toHashSet() ?: setOf()

    @JvmStatic
    fun main(args: Array<String>) {
        startKoin {
            environmentProperties()
            modules(applicationModule)

            val importService: ImportService = koin.get()
            val groupingService: GroupingService = koin.get()
            val transformService: TransformService = koin.get()
            val outputFormatter: OutputFormatter = koin.get()
            val printer: Printer = koin.get()

            val issues = importService.findIssues()
            val groupedIssues = groupingService.groupByTime(issues)
            val resultLines = transformService.transform(groupedIssues)
            printer.write(outputFormatter.format(resultLines))
        }
    }
}
