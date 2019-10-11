package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
class RateLimits(val remaining: Long, val reset: Long) {
    companion object {
        private const val rateNodeName = "rate"
        private const val remainingNodeName = "remaining"
        private const val resetNodeName = "reset"

        @JsonCreator
        @JvmStatic
        fun createFromJson(jsonNode: JsonNode): RateLimits {
            val rateNode = jsonNode[rateNodeName] ?: throw NodeNotFoundException(rateNodeName)
            return RateLimits(
                rateNode[remainingNodeName]?.asLong() ?: throw NodeNotFoundException(remainingNodeName),
                rateNode[resetNodeName]?.asLong() ?: throw NodeNotFoundException(resetNodeName)
            )
        }
    }
}

private class NodeNotFoundException(expected: String) : RuntimeException("Expected to find $expected node")
