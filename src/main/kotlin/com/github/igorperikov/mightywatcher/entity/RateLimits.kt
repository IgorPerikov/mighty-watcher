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
            val rateNode = jsonNode[rateNodeName] ?: throw WrongNodeNameException(rateNodeName)
            return RateLimits(
                rateNode[remainingNodeName]?.asLong() ?: throw WrongNodeNameException(remainingNodeName),
                rateNode[resetNodeName]?.asLong() ?: throw WrongNodeNameException(resetNodeName)
            )
        }
    }
}

private class WrongNodeNameException(expectedNodeName: String) :
    RuntimeException("Expected to find $expectedNodeName node")
