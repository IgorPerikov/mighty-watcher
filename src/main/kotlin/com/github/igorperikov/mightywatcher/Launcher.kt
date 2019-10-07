package com.github.igorperikov.mightywatcher

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import com.github.igorperikov.mightywatcher.external.GithubApiClient
import com.github.igorperikov.mightywatcher.external.RestGithubApiClient
import com.github.igorperikov.mightywatcher.service.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import org.koin.core.context.startKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.slf4j.LoggerFactory

typealias Issues = MutableList<Issue>

const val INCLUDE_LANG_ENV_NAME = "INCLUDE"
const val EXCLUDE_REPOS_ENV_NAME = "EXCLUDE"
const val PARALLELISM_LEVEL_ENV_NAME = "PARALLELISM"
const val DEFAULT_PARALLELISM_LEVEL = "10"
const val DAYS_SINCE_LAST_UPDATE_ENV_NAME = "DAYS"
const val DEFAULT_DAYS_SINCE_LAST_UPDATE = "90"
const val TOKEN_ENV_NAME = "TOKEN"

object Launcher {
    @JvmStatic
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun initHttpClient(githubToken: String): HttpClient {
        return HttpClient(Apache) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    findAndRegisterModules()
                    registerModule(JavaTimeModule())
                }
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                }
                header("Accept", "application/vnd.github.v3+json")
                header("User-Agent", "IgorPerikov/mighty-watcher")
                header("Authorization", "token $githubToken")
            }
        }
    }

    val applicationModule = module {
        single { initHttpClient(getProperty(TOKEN_ENV_NAME)) }
        single { RestGithubApiClient(get()) as GithubApiClient }
        single { GithubService(get(), getSetProperty(INCLUDE_LANG_ENV_NAME), getSetProperty(EXCLUDE_REPOS_ENV_NAME)) }
        single { EasyLabelsStorage() }
        single { LabelsService(get(), get()) }
        single {
            ImportService(get(), get(),
                    getProperty(PARALLELISM_LEVEL_ENV_NAME, DEFAULT_PARALLELISM_LEVEL).toInt(),
                    getProperty(DAYS_SINCE_LAST_UPDATE_ENV_NAME, DEFAULT_DAYS_SINCE_LAST_UPDATE).toLong()
            )
        }
        single { GroupingService.withDefaultTimeGroups() }
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

            val issues = importService.findIssues()
            val groupedIssues = groupingService.groupByTime(issues)
            printResult(groupedIssues)
        }
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
