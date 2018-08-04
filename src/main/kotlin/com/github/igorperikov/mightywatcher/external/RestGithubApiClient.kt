package com.github.igorperikov.mightywatcher.external

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.igorperikov.mightywatcher.entity.Issue
import com.github.igorperikov.mightywatcher.entity.Repository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * v3 entity api client, full specification - https://developer.entity.com/v3/
 */
class RestGithubApiClient(githubToken: String) : GithubApiClient {
    private val httpClient = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    private val authHeaderValue = "token $githubToken"

    private val mapper = jacksonObjectMapper().findAndRegisterModules()

    override fun getIssues(repoFullName: String, label: String): Set<Issue> {
        val (owner, name) = repoFullName.split("/")
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
            .header("Authorization", authHeaderValue)
            .build()
        val response: Response = httpClient.newCall(request).execute()
        val jsonBody = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue(jsonBody)
    }

    override fun getStarredRepositories(username: String): Set<Repository> {
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
            .header("Authorization", authHeaderValue)
            .build()

        val response: Response = httpClient.newCall(request).execute()
        val jsonBody = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue(jsonBody)
    }
}
