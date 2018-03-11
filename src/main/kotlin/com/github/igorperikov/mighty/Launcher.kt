package com.github.igorperikov.mighty

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object Launcher {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
    }

    private val authorizationHeaderValue: String by lazy {
        "token ${readResourceFile("token")}"
    }

    private val username: String by lazy {
        readResourceFile("username")
    }

    private val mapper = jacksonObjectMapper()

    private val importRepositories = (System.getProperty("import.repositories") ?: "false").toBoolean()

    @JvmStatic
    fun main(args: Array<String>) {
        println("start at  ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
        if (importRepositories) {
            val repositories = getStarredRepositories()
            val languages = mutableSetOf<String>()
            val ignoredRepos = mutableSetOf<String>()

            readResourceFile("languages")
                    .lines()
                    .forEach { languages.add(it.toLowerCase()) }

            readResourceFile("ignored-repos")
                    .lines()
                    .forEach { ignoredRepos.add(it) }

            val file = File("src/main/resources/tracked-repos")
            file.delete()
            file.createNewFile()

            repositories
                    .filter { it.hasIssues }
                    .filter { it.fullName !in ignoredRepos }
                    .filter { it.language?.toLowerCase() in languages }
                    .forEach { file.appendText(it.fullName + "\r\n") }
        }

        val labelsSet = readResourceFile("labels").lines().toSet()
        val issues = mutableSetOf<Issue>()
        val ignoredIssues = readResourceFile("ignored-issues").lines().toSet()

        readResourceFile("tracked-repos")
                .lines()
                .filter { it.isNotBlank() }
                .forEach { repoFullName ->
                    labelsSet.forEach { label ->
                        val notIgnoredIssues = getIssues(repoFullName, label).filter { it.htmlUrl !in ignoredIssues }
                        issues.addAll(notIgnoredIssues)
                    }
                }

        val resultFile = File("src/main/resources/result")
        resultFile.delete()
        resultFile.createNewFile()

        issues.forEach {
            resultFile.appendText(it.toString() + "\r\n")
        }
        println("finish at ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
    }

    private fun getIssues(repoFullName: String, label: String): Set<Issue> {
        val owner = repoFullName.split("/")[0]
        val name = repoFullName.split("/")[1]
        val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.github.com")
                .addPathSegment("repos")
                .addPathSegment(owner)
                .addPathSegment(name)
                .addPathSegment("issues")
                .addQueryParameter("assignee", "none")
                .addQueryParameter("since", Instant.now().minus(Duration.ofDays(365)).toString())
                .addQueryParameter("labels", label)
                .build()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", authorizationHeaderValue)
                .build()
        val response: Response = httpClient.newCall(request).execute()
        val jsonBody: String = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue(jsonBody)
    }

    private fun getStarredRepositories(): Set<Repository> {
        val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.github.com")
                .addPathSegment("users")
                .addPathSegment(username)
                .addPathSegment("starred")
                .build()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", authorizationHeaderValue)
                .build()

        val response: Response = httpClient.newCall(request).execute()
        val jsonBody: String = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue(jsonBody)
    }

    private fun readResourceFile(name: String): String = Launcher::class.java.classLoader.getResource(name).readText()
}
