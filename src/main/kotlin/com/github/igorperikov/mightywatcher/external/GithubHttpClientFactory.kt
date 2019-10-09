package com.github.igorperikov.mightywatcher.external

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import kotlinx.coroutines.runBlocking

fun initHttpClient(githubToken: String): HttpClient {
    val httpClient =  HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                findAndRegisterModules()
                registerModule(JavaTimeModule())
            }
        }

        xRateLimit {
            startDegradeFrom = 1000
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

    //call to /rate_limit doesn't count as search but it returns X-Rate-Limit headers
    //so XRateLimitInterceptor has relevant XRateLimit before first search call is made
    runBlocking {
        httpClient.get<Unit> {
            url {
                path("rate_limit")
            }
        }
    }

    return httpClient
}