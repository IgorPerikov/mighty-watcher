package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class XRateLimit(
        @JsonProperty("limit") val limit: Long,
        @JsonProperty("remaining") val remaining: Long,
        @JsonProperty("reset") val reset: Long
)