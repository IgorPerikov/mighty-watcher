package com.github.igorperikov.mighty

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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

    private val importStarredRepos = true

    @JvmStatic
    fun main(args: Array<String>) {
        if (importStarredRepos) {
            val repositories = gatherRepos()
            val languages: MutableSet<String> = mutableSetOf()

            readResourceFile("languages").lines().forEach {
                languages.add(it.toLowerCase())
            }

            repositories
                    .filter { it.hasIssues }
                    .filter { it.language?.toLowerCase() in languages }
                    .forEach { println(it.fullName) }
        }
    }

    private fun gatherRepos(): Iterable<RepoDefinition> {
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
        val content: String = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue<List<RepoDefinition>>(content)
    }

    private fun readResourceFile(name: String): String = Launcher::class.java.classLoader.getResource(name).readText()
}
