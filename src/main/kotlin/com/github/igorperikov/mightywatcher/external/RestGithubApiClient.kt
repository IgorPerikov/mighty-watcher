package com.github.igorperikov.mightywatcher.external

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.Label
import com.github.igorperikov.mightywatcher.entity.Repository
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * v3 entity api client, full specification - https://developer.entity.com/v3/
 */
class RestGithubApiClient(githubToken: String) : GithubApiClient {
    val CALL_TIMEOUT : Long = 15_000

    val httpClient = HttpClient(Apache) {
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

    override fun getStarredRepositories(): List<Repository> {
        return runBlocking {
            withTimeout(CALL_TIMEOUT) {
                httpClient.get<List<Repository>> {
                    url {
                        path("user", "starred")
                        parameter("per_page", "3000")
                    }
                }
            }
        }
    }

    override fun getRepositoryLabels(owner: String, repo: String): List<Label> {
        return runBlocking {
            withTimeout(CALL_TIMEOUT) {
                httpClient.get<List<Label>> {
                    url {
                        path("repos", owner, repo, "labels")
                        parameter("per_page", "1000")
                    }
                }
            }
        }
    }

    override fun getIssues(owner: String, repo: String, label: String, since: String): Issues {
        return runBlocking {
            withTimeout(CALL_TIMEOUT) {
                httpClient.get<Issues> {
                    url {
                        path("repos", owner, repo, "issues")
                        parameter("assignee", "none")
                        parameter("since", since)
                        parameter("labels", label)
                        parameter("sort", "created")
                        parameter("per_page", "2000")
                    }
                }
            }
        }
    }

    companion object {
        private const val TOKEN_ENV_NAME = "TOKEN"

        fun fromEnv() =
            RestGithubApiClient(requireNotNull(System.getenv(TOKEN_ENV_NAME), { "$TOKEN_ENV_NAME should be set" }))
    }
}
