package com.github.igorperikov.mightywatcher.external

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Repository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * v3 entity api client, full specification - https://developer.entity.com/v3/
 */
// TODO: https://github.com/IgorPerikov/mighty-watcher/issues/26
class RestGithubApiClient(githubToken: String) : GithubApiClient {
    private val httpClient = OkHttpClient.Builder().callTimeout(5, TimeUnit.SECONDS).build()
    private val jsonMapper = jacksonObjectMapper()
        .findAndRegisterModules()
        .registerModule(JavaTimeModule())
    private val authHeaderValue = "token $githubToken"

    override fun getStarredRepositories(): List<Repository> {
        return proceedRequestForUrl {
            HttpUrl.Builder()
                .scheme("https")
                .host("api.github.com")
                .addPathSegment("user")
                .addPathSegment("starred")
                .addQueryParameter("per_page", "1000")
                .build()
        }
    }

    override fun getIssues(repoFullName: String, label: String, since: String): Issues {
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
                .addQueryParameter("since", since)
                .addQueryParameter("labels", label)
                .addQueryParameter("per_page", "1000")
                .build()
        }
    }

    private inline fun <reified T> proceedRequestForUrl(urlSupplier: () -> HttpUrl): MutableList<T> {
        val request = buildRequest(urlSupplier)
        val jsonBody = getResponseBody(request)
        return jsonMapper.readValue(
            jsonBody,
            jsonMapper.typeFactory.constructCollectionType(ArrayList::class.java, T::class.java)
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
        val response = httpClient.newCall(request).execute()
        return response.body()?.string() ?: throw RuntimeException("Empty response body")
    }
}
