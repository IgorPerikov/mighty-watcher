package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class XRateResources(
        @JsonProperty("resources") val resources: Map<String, XRateLimit>,
        @JsonProperty("rate") val rate: XRateLimit
)
