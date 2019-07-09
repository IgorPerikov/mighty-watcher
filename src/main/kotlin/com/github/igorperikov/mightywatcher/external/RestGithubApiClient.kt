package com.github.igorperikov.mightywatcher.external

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
    private val jsonMapper = jacksonObjectMapper().findAndRegisterModules()
    private val authHeaderValue = "token $githubToken"

    override fun getStarredRepositories(): Set<Repository> {
        return proceedRequestForUrl {
            HttpUrl.Builder()
                    .scheme("https")
                    .host("api.github.com")
                    .addPathSegment("user")
                    .addPathSegment("starred")
                    .build()
        }
    }

    override fun getIssues(repoFullName: String, label: String): Set<Issue> {
        return proceedRequestForUrl {
            val (owner, name) = repoFullName.split("/")
            HttpUrl.Builder()
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
        }
    }

    private inline fun <reified T> proceedRequestForUrl(urlSupplier: () -> HttpUrl): Set<T> {
        val request = buildRequest(urlSupplier)
        val jsonBody = getResponseBody(request)
        return jsonMapper.readValue(
                jsonBody,
                jsonMapper.typeFactory.constructCollectionType(HashSet::class.java, T::class.java)
        )
    }

    private inline fun buildRequest(urlSupplier: () -> HttpUrl): Request {
        return Request.Builder()
                .url(urlSupplier())
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", authHeaderValue)
                .build()
    }

    private fun getResponseBody(request: Request): String {
        val response: Response = httpClient.newCall(request).execute()
        return response.body()?.string() ?: throw RuntimeException("Empty response body")
    }
}
