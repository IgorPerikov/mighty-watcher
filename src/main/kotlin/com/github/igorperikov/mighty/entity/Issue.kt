package com.github.igorperikov.mighty.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issue(
        @JsonProperty("html_url") val htmlUrl: String,
        val title: String
) {
    override fun toString() = "$title  $htmlUrl"
}
