package com.github.igorperikov.mightywatcher.external

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.response.HttpResponsePipeline
import io.ktor.http.Headers
import io.ktor.util.AttributeKey
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import kotlin.math.max

//The maximum number of requests you're permitted to make per hour.
const val X_RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Limit"
const val X_RATE_LIMIT_LIMIT_DEFAULT_VALUE = 5000L

//The number of requests remaining in the current rate limit window.
const val X_RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining"
const val X_RATE_LIMIT_REMAINING_DEFAULT_VALUE = 5000L

// The time at which the current rate limit window resets in UTC epoch seconds.
const val X_RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset"
val X_RATE_LIMIT_RESET_DEFAULT_VALUE = Instant.now().epochSecond

val DEFAULT_X_RATE = XRateLimit(
        X_RATE_LIMIT_LIMIT_DEFAULT_VALUE,
        X_RATE_LIMIT_REMAINING_DEFAULT_VALUE,
        X_RATE_LIMIT_RESET_DEFAULT_VALUE
)

data class XRateLimit(val limit: Long, val remaining: Long, val reset: Long)

class XRateLimitInterceptor(
        val startDegradeFrom : Long,
        @Volatile var rate : XRateLimit = DEFAULT_X_RATE
) {

    class Config(
            /*
                Start from this remaining value every call will be delayed for (reset - now()) / remaining
             */
            var startDegradeFrom : Long? = null
    )

    companion object Feature : HttpClientFeature<Config, XRateLimitInterceptor> {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)

        override val key: AttributeKey<XRateLimitInterceptor> = AttributeKey("XRateLimitInterceptor")

        override fun prepare(block: Config.() -> Unit): XRateLimitInterceptor {
            val config = Config().apply(block)
            return XRateLimitInterceptor(config.startDegradeFrom ?: 0)
        }

        override fun install(feature: XRateLimitInterceptor, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) {
                parseRate(context.response.headers)?.let {
                    val oldRate = feature.rate
                    if (oldRate.remaining > feature.startDegradeFrom && it.remaining <= feature.startDegradeFrom) {
                        val reset = Instant.ofEpochSecond(it.reset)
                        log.warn("GitHub Rest API limit is about to be reached. " +
                                "Until $reset there are ${it.remaining} remaining. " +
                                "The following calls will be slowed deliberately.")
                    }
                    feature.rate = it
                }
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                val currentRate = feature.rate
                if (currentRate.remaining <= feature.startDegradeFrom) {
                    delay(calculateDelay(currentRate))
                }
            }
        }

        fun parseRate(headers : Headers) : XRateLimit? {
            val limit = headers[X_RATE_LIMIT_LIMIT_HEADER]?.toLongOrNull()
            val remaining = headers[X_RATE_LIMIT_REMAINING_HEADER]?.toLongOrNull()
            val reset = headers[X_RATE_LIMIT_RESET_HEADER]?.toLongOrNull()

            if (limit != null && remaining != null && reset != null
                    && limit >= 0 && remaining >= 0 && reset >= 0) {
                return XRateLimit(limit, remaining, reset)
            }
            return null
        }

        fun calculateDelay(rate : XRateLimit, clock: Clock = Clock.systemUTC()) : Long {
            //Note XRateLimit.reset is in epoch seconds while returned value is delay in milliseconds
            return max(0, rate.reset - Instant.now(clock).epochSecond) * 1000 / max(1, rate.remaining)
        }
    }
}

fun HttpClientConfig<*>.xRateLimit(config: XRateLimitInterceptor.Config.() -> Unit) {
    install(XRateLimitInterceptor) {
        config()
    }
}