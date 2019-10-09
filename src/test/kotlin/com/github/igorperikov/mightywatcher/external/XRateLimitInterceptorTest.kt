package com.github.igorperikov.mightywatcher.external

import com.github.igorperikov.mightywatcher.external.XRateLimitInterceptor.Feature.calculateDelay
import com.github.igorperikov.mightywatcher.external.XRateLimitInterceptor.Feature.parseRate
import io.ktor.http.Headers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


class XRateLimitInterceptorTest {

    private fun initHeaders(limit: String?, remaining: String?, reset: String?) : Headers {
        return Headers.build {
            limit?.let { append(X_RATE_LIMIT_LIMIT_HEADER, it) }
            remaining?.let { append(X_RATE_LIMIT_REMAINING_HEADER, it) }
            reset?.let { append(X_RATE_LIMIT_RESET_HEADER, it) }
        }
    }

    @Test
    fun `headers are positive long`() {
        Assertions.assertNull(parseRate(initHeaders(null, "1", "0")))
        Assertions.assertNull(parseRate(initHeaders("1", "str", "1")))
        Assertions.assertNull(parseRate(initHeaders("0", "1", "-1")))
        Assertions.assertEquals(
                XRateLimit(Long.MAX_VALUE,1,0),
                parseRate(initHeaders(Long.MAX_VALUE.toString(), "1", "0"))
        )
    }

    @Test
    fun `for 'reset' in the past delay should be zero`() {
        val reset = Instant.parse("2000-01-01T00:00:00.00Z")
        val now = Instant.parse("2000-01-01T00:00:01.00Z")
        val clock = Clock.fixed(now, ZoneId.of("UTC"))

        val rate = XRateLimit(0, 1, reset.epochSecond)
        Assertions.assertEquals( 0, calculateDelay(rate, clock))
    }

    @Test
    fun `for zero 'remaining' delay should be milliseconds until reset`() {
        val clock = Clock.fixed(Instant.ofEpochSecond(0), ZoneId.of("UTC"))
        val rate = XRateLimit(0, 0, 123)
        Assertions.assertEquals( 123000, calculateDelay(rate, clock))
    }

    @Test
    fun `delay should be 'reset' and now() difference divided by 'remaining'`() {
        val clock = Clock.fixed(Instant.ofEpochSecond(0), ZoneId.of("UTC"))
        Assertions.assertEquals( 1000, calculateDelay(XRateLimit(0, 2,  2), clock))
        Assertions.assertEquals( 2000, calculateDelay(XRateLimit(0, 5,  10), clock))
        Assertions.assertEquals( 3000, calculateDelay(XRateLimit(0, 3,  9), clock))
    }
}