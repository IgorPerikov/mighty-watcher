package external

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ResourceFilesUtils
import entity.Issue
import entity.Repository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

class RestClient : Client {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    private val authorizationHeaderValue: String by lazy {
        "token ${ResourceFilesUtils.readResourceFile("token")}"
    }

    private val mapper = jacksonObjectMapper().findAndRegisterModules()

    override fun getIssues(repoFullName: String, label: String): Set<Issue> {
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
            .header("Authorization", authorizationHeaderValue)
            .build()

        val response: Response = httpClient.newCall(request).execute()
        val jsonBody: String = response.body()?.string() ?: throw RuntimeException("Empty response body")
        return mapper.readValue(jsonBody)
    }
}
